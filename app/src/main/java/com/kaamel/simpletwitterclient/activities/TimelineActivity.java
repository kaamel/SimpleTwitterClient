package com.kaamel.simpletwitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.SimpleTwitterApplication;
import com.kaamel.simpletwitterclient.TweetAdapter;
import com.kaamel.simpletwitterclient.TwitterClient;
import com.kaamel.simpletwitterclient.databinding.ActivityTimelineBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    ActivityTimelineBinding binding;

    private static final TwitterClient client = SimpleTwitterApplication.getRestClient();
    Gson gson = new GsonBuilder().create();

    private static List<Tweet> tweets;
    private RecyclerView rvTweets;
    TweetAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_timeline);

        if (tweets == null)
            tweets = new ArrayList<>();

        rvTweets = binding.rvTweets;
        adapter = new TweetAdapter(tweets);
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);
        populateTimeline();
    }

    private void populateTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                Gson gson = new GsonBuilder().create();
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                Log.d("TwitterClient", tweet.body);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                int count = response.length();
                tweets.clear();
                for (int i = 0; i<count; i++) {
                    try {
                        tweets.add(gson.fromJson(response.get(i).toString(), Tweet.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                Log.d("TwitterClient", throwable.getLocalizedMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                Log.d("TwitterClient", throwable.getLocalizedMessage());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                Log.d("TwitterClient", throwable.getLocalizedMessage());
            }
        });
    }
}
