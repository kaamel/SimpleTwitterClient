package com.kaamel.simpletwitterclient.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.activities.MainActivityToTwitterFragmentsCallbacks;
import com.kaamel.simpletwitterclient.activities.OnFragmentInteractionListener;
import com.kaamel.simpletwitterclient.adapters.EndlessRecyclerViewScrollListener;
import com.kaamel.simpletwitterclient.adapters.TweetAdapter;
import com.kaamel.simpletwitterclient.databinding.FragmentTweetsListBinding;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TweetsHomeListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TweetsHomeListFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, MainActivityToTwitterFragmentsCallbacks {

    FragmentTweetsListBinding binding;
    private ActionBar sab;

    private List<Tweet> tweets;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    EndlessRecyclerViewScrollListener scrollListener;

    boolean editing = false;

    LinearLayoutManager linearLayoutManager;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TITLE = "title";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String title;

    private OnFragmentInteractionListener mListener;

    public TweetsHomeListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param title The tab/frament title.
     * @return A new instance of fragment TweetsListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TweetsHomeListFragment newInstance(String param1, String title) {
        TweetsHomeListFragment fragment = new TweetsHomeListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            title = getArguments().getString(TITLE);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_tweets_list, container, false);
        View view = binding.getRoot();

        tweets = Tweet.recentTweets(20);
        if (tweets == null)
            tweets = new ArrayList<>();

        rvTweets = binding.rvTweets;
        swipeRefreshLayout = binding.srLayout;
        adapter = new TweetAdapter(tweets);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                int p = tweets.size();
                if (p <1)
                    populateTimeline(0);
                else
                    populateTimeline(tweets.get(tweets.size()-1).getId());

            }
        };
        rvTweets.setOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(this);

        adapter.notifyDataSetChanged();

        onRefresh();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onReceivedTweet(Tweet tweet) {
        tweets.add(0, tweet);
        tweet.save();
        adapter.notifyItemInserted(0);
        linearLayoutManager.smoothScrollToPosition(rvTweets, new RecyclerView.State(), 0);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onReceivedTweets(List<Tweet> ts) {

        swipeRefreshLayout.setRefreshing(false);
        int cs = tweets.size();
        if (ts.size()>0) {
            tweets.addAll(ts);
            if (cs == 0)
                Tweet.replaceAllTweets(tweets);
            else
                Tweet.addUpdateTweets(tweets);

            adapter.notifyItemRangeInserted(cs, tweets.size());
        }
        else if (cs == 0) {
            tweets.addAll(Tweet.recentTweets(20));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClientError(String message) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        if (tweets.size() == 0) {
            tweets.addAll(Tweet.recentTweets(20));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClientErrors(List<String> errors) {
        swipeRefreshLayout.setRefreshing(false);
        if (errors != null && errors.size()>0)
            Toast.makeText(getContext(), errors.get(0), Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(getContext(), "Multiple failures", Toast.LENGTH_LONG).show();
        }
        if (tweets.size() == 0) {
            tweets.addAll(Tweet.recentTweets(20));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public Tweet getTweet(int position) {
        return tweets.get(position);
    }


    private void populateTimeline(long maxId) {

        if (!Utils.isNetworkAvailable(getContext()) || !Utils.isOnline()) {
            Toast.makeText(getContext(), "Currently offline", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (maxId < 1) {
            tweets.clear();
            //TweetModel.removeall();
            scrollListener.resetState();
            adapter.notifyDataSetChanged();
            mListener.getHomeTweets(maxId, title);
            swipeRefreshLayout.setRefreshing(true);
        }
        else {
            mListener.getHomeTweets(maxId, title);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        populateTimeline(0);
    }
}
