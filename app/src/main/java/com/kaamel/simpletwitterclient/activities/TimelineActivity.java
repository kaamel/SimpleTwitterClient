package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kaamel.simpletwitterclient.EndlessRecyclerViewScrollListener;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.adapters.TweetAdapter;
import com.kaamel.simpletwitterclient.databinding.ActivityTimelineBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;

import java.util.ArrayList;
import java.util.List;

public class TimelineActivity extends AppCompatActivity implements TwitterClientHelper.CallbackWithTweets, SwipeRefreshLayout.OnRefreshListener  {

    ActivityTimelineBinding binding;

    private static List<Tweet> tweets;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout = binding.srLayout;
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

        swipeRefreshLayout.setOnRefreshListener(this);

        if (firstTime)
            populateTimeline(0);

        Intent intent = getIntent();
        if (intent != null) {
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
    }

    private void composeTweet(String body) {
        //// TODO: 9/28/17
        Toast.makeText(this, body, Toast.LENGTH_LONG).show();
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
}
