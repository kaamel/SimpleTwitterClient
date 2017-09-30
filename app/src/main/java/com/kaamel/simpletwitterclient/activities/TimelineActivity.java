package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.kaamel.simpletwitterclient.Comp0seTweetDialogFragment;
import com.kaamel.simpletwitterclient.EndlessRecyclerViewScrollListener;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.adapters.TweetAdapter;
import com.kaamel.simpletwitterclient.databinding.ActivityTimelineBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements
        Comp0seTweetDialogFragment.OnTweetComposerUpdateListener, 
        TwitterClientHelper.CallbackWithTweets, SwipeRefreshLayout.OnRefreshListener  {

    ActivityTimelineBinding binding;
    private ActionBar sab;

    private static List<Tweet> tweets;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    EndlessRecyclerViewScrollListener scrollListener;

    private static final TwitterClientHelper twitterClientHelper = new TwitterClientHelper();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        Toolbar myToolbar = binding.toolbar;
        setSupportActionBar(myToolbar);

        sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayShowHomeEnabled(true);
            sab.setLogo(R.drawable.twitter_logo_blue);
            sab.setTitle("Home Timeline");
            sab.setDisplayUseLogoEnabled(true);
            sab.setDisplayShowTitleEnabled(true);
        }

        boolean firstTime = false;
        if (tweets == null) {
            tweets = new ArrayList<>();
            firstTime = true;
        }

        rvTweets = binding.rvTweets;
        swipeRefreshLayout = binding.srLayout;
        adapter = new TweetAdapter(tweets);
        linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeline(tweets.get(tweets.size()-1).id);
            }
        };
        rvTweets.setOnScrollListener(scrollListener);

        swipeRefreshLayout.setOnRefreshListener(this);

        if (firstTime)
            populateTimeline(0);

        Intent intent = getIntent();
        if (intent != null) {
            processImplicitIntent(intent);
        }
    }

    private void processImplicitIntent(Intent intent) {
        String type = intent.getStringExtra("type");
        String action = intent.getStringExtra("action");
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                // Make sure to check whether returned data will be null.
                String body = intent.getStringExtra("body");
                String title = intent.getStringExtra("title");
                if (body != null) {
                    if (title != null)
                        composeTweet(title + " " + body);
                    else
                        composeTweet(body);
                }
                else if (title != null)
                    composeTweet(title);
            }
        }
    }

    private void composeTweet(String body) {
        DialogFragment dialog = Comp0seTweetDialogFragment.newInstance(body);
        dialog.show(getSupportFragmentManager(), body);
    }

    public void onClickComposeTweet(View view) {
        String savedText = "";
        //// TODO: 9/29/17 check to see if there is a saved text is available; if not start new
        composeTweet(savedText);
    }

    private void populateTimeline(long maxId) {
        if (maxId < 1) {
            tweets.clear();
            scrollListener.resetState();
            adapter.notifyDataSetChanged();
            twitterClientHelper.getHomeTimeline(this);
        }
        else {
            twitterClientHelper.getHomeTimeline(this, maxId);
        }
    }


    @Override
    public void onSuccess(int statusCode, Tweet tweet) {
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        linearLayoutManager.smoothScrollToPosition(rvTweets, new RecyclerView.State(), 0);
    }

    @Override
    public void onSuccess(int statusCode, List<Tweet> ts) {
        swipeRefreshLayout.setRefreshing(false);
        tweets.addAll(ts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int statusCode, String responseString, Throwable throwable) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, responseString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailure(int statusCode, List<String> errors, Throwable throwable) {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        populateTimeline(0);
    }

    @Override
    public void onUpdate(int status, String body) {
        if (status == 1)
            twitterClientHelper.postTweet(body, this);
    }
}
