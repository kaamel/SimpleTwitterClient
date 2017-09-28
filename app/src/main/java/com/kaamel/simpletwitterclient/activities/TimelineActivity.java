package com.kaamel.simpletwitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kaamel.simpletwitterclient.EndlessRecyclerViewScrollListener;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.adapters.TweetAdapter;
import com.kaamel.simpletwitterclient.databinding.ActivityTimelineBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements TwitterClientHelper.CallbackWithTweets {

    ActivityTimelineBinding binding;

    private static List<Tweet> tweets;
    private RecyclerView rvTweets;
    TweetAdapter adapter;

    EndlessRecyclerViewScrollListener scrollListener;

    private static final TwitterClientHelper twitterClientHelper = new TwitterClientHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        boolean firstTime = false;
        if (tweets == null) {
            tweets = new ArrayList<>();
            firstTime = true;
        }

        rvTweets = binding.rvTweets;
        adapter = new TweetAdapter(tweets);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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

        if (firstTime)
            populateTimeline(0);
    }

    private void populateTimeline(long maxId) {
        if (maxId < 1) {
            tweets.clear();
            scrollListener.resetState();
            twitterClientHelper.getHomeTimeline(this);
        }
        else {
            twitterClientHelper.getHomeTimeline(this, maxId);
        }
    }


    @Override
    public void onSuccess(int statusCode, Tweet tweet) {

    }

    @Override
    public void onSuccess(int statusCode, List<Tweet> ts) {
        tweets.addAll(ts);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(int statusCode, String responseString, Throwable throwable) {
        if (statusCode == 429) {

        }
    }

    @Override
    public void onFailure(int statusCode, List<String> errors, Throwable throwable) {

    }
}
