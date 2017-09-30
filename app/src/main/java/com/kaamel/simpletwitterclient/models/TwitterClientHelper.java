package com.kaamel.simpletwitterclient.models;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaamel.simpletwitterclient.SimpleTwitterApplication;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by kaamel on 9/28/17.
 */

public class TwitterClientHelper {

    private static final TwitterClient client = SimpleTwitterApplication.getRestClient();
    Gson gson = new GsonBuilder().create();

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets, long sinceId, long maxId) {
        getHomeTimeline(callbackWithTweets, sinceId, maxId, 3, 500);
    }

    private void getHomeTimeline(final CallbackWithTweets callbackWithTweets, final long sinceId, final long maxId, final int retry, int delay) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                callbackWithTweets.onSuccess(statusCode, tweet);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                int count = response.length();
                List<Tweet> tweets = new ArrayList<>();
                for (int i = 0; i<count; i++) {
                    try {
                        tweets.add(gson.fromJson(response.get(i).toString(), Tweet.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callbackWithTweets.onSuccess(statusCode, tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callbackWithTweets.onFailure(statusCode, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 429) {
                    if (retry > 0) {
                        Handler handler = new Handler();
                        Runnable runnableCode = () -> getHomeTimeline(callbackWithTweets, sinceId, maxId, retry-1, 2 * delay);
                        handler.postDelayed(runnableCode, delay);
                        return;
                    }
                    callbackWithTweets.onFailure(statusCode, "Too many requests. Please wait a few minutes and try again", throwable);
                }
                List<TwitterErrors.TwitterError> errors = gson.fromJson(errorResponse.toString(), TwitterErrors.class).errors;
                callbackWithTweets.onFailure(statusCode, errors.get(0).errorMessage, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                int count = errorResponse.length();
                List<String> errorStrings = new ArrayList<>();
                for (int i = 0; i<count; i++) {
                    try {
                        errorStrings.add(gson.fromJson(errorResponse.get(i).toString(), String.class));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                callbackWithTweets.onFailure(statusCode, errorStrings, throwable);
            }
        }, sinceId, maxId);
    }

    public void postTweet(String text, CallbackWithTweets callbackWithTweets) {
        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                callbackWithTweets.onSuccess(statusCode, tweet);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d("Post Tweet", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("Post Tweet", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Post Tweet", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("Post Tweet", errorResponse.toString());
            }
        }, text);
    }

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets, long maxId) {
        getHomeTimeline(callbackWithTweets, 1, maxId);
    }

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets) {
        getHomeTimeline(callbackWithTweets, 1, -1);
    }

    public interface CallbackWithTweets {
        void onSuccess(int statusCode, Tweet tweet);
        void onSuccess(int statusCode, List<Tweet> tweets);
        void onFailure(int statusCode, String errorString, Throwable throwable);
        void onFailure(int statusCode, List<String> errorStrings, Throwable throwable);
    }
}
