package com.kaamel.simpletwitterclient.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.adapters.EndlessRecyclerViewScrollListener;
import com.kaamel.simpletwitterclient.adapters.TweetAdapter;
import com.kaamel.simpletwitterclient.databinding.ActivityTimelineBinding;
import com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment;
import com.kaamel.simpletwitterclient.models.TweetModel;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.utils.Utils;

import java.util.List;

public class TimelineActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnTweetComposerUpdateListener,
        TwitterClientHelper.CallbackWithTweets, SwipeRefreshLayout.OnRefreshListener  {

    private static final int SHOW_DETAIL_INTENT = 100;
    ActivityTimelineBinding binding;
    private ActionBar sab;

    private static List<Tweet> tweets;
    private RecyclerView rvTweets;
    private TweetAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    SharedPreferences sharedPref;

    EndlessRecyclerViewScrollListener scrollListener;

    boolean editing = false;

    private static final TwitterClientHelper twitterClientHelper = new TwitterClientHelper();
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        Toolbar myToolbar = binding.toolbar;
        setSupportActionBar(myToolbar);

        sharedPref = getSharedPreferences(
                /*getString(R.string.preference_file_key)*/ "simpletwitterclient", Context.MODE_PRIVATE);
        sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayShowHomeEnabled(true);
            sab.setLogo(R.drawable.ic_twitter_logo_blue_48);
            sab.setTitle("Home");
            sab.setDisplayUseLogoEnabled(true);
            sab.setDisplayShowTitleEnabled(true);
        }

        boolean firstTime = false;
        if (tweets == null) {
            //just started running the app
            TweetModel.loadTweets();
            firstTime = true;
        }
        tweets = TweetModel.getTweets();

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

        adapter.notifyDataSetChanged();
        if (firstTime)
            populateTimeline(0);

        Intent intent = getIntent();
        if (intent != null) {
            processImplicitIntent(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_DETAIL_INTENT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String body = data.getExtras().getString("body");
                int status = data.getExtras().getInt("status");
                onUpdate(status, body);
            }
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
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance(body, (body==null || body.length() ==0)?"New tweet":"Continue editing");
        dialog.show(getSupportFragmentManager(), body);
    }

    public void onClickComposeTweet(View view) {
        if (!editing)
            composeTweet(restoreTweet());
        editing = true;
    }

    private void populateTimeline(long maxId) {
        if (!Utils.isNetworkAvailable(this) || !Utils.isOnline()) {
            Toast.makeText(this, "Currently offline", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (maxId < 1) {
            tweets.clear();
            TweetModel.removeall();
            scrollListener.resetState();
            adapter.notifyDataSetChanged();
            twitterClientHelper.getHomeTimeline(this);
        }
        else {
            twitterClientHelper.getHomeTimeline(this, maxId);
        }
    }

    private void saveTweet(String body) {
        sharedPref.edit().putString("saved_tweet", body).apply();
    }

    private String restoreTweet() {
        String body = sharedPref.getString("saved_tweet", null);
        sharedPref.edit().remove("saved_tweet").apply();
        return body;
    }


    @Override
    public void onSuccess(int statusCode, Tweet tweet) {
        tweets.add(0, tweet);
        new TweetModel(tweet).save();
        adapter.notifyItemInserted(0);
        linearLayoutManager.smoothScrollToPosition(rvTweets, new RecyclerView.State(), 0);
    }

    @Override
    public void onSuccess(int statusCode, List<Tweet> ts) {
        swipeRefreshLayout.setRefreshing(false);
        tweets.addAll(ts);
        TweetModel.saveAll(ts);
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
        if (status == STATUS_TWEET)
            twitterClientHelper.postTweet(body, this);
        else if (status == STATUS_SAVE)
            saveTweet(body);

        editing = false;
    }


    public void onTweetClicked(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("tweet", tweets.get(position));
        startActivityForResult(intent, SHOW_DETAIL_INTENT);
    }
}
