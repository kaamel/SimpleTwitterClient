package com.kaamel.simpletwitterclient.models;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaamel.simpletwitterclient.SimpleTwitterApplication;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.TweetJsonDeserializer;
import com.kaamel.simpletwitterclient.twitteritems.TweetJsonSerializer;
import com.kaamel.simpletwitterclient.twitteritems.TwitterErrors;
import com.kaamel.simpletwitterclient.twitteritems.User;
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

    public void getUserTimeline(final CallbackWithTweets callbackWithTweets, long uid, String twitterHanele, long maxId, String frgName) {
        getUserTimeline(callbackWithTweets, uid, twitterHanele, 1, maxId, 3, 500, frgName);
    }

    private void getUserTimeline(CallbackWithTweets callbackWithTweets, long uid, String twitterHanele, int sinceId, long maxId, int retry, int delay, String frgName) {
        client.getUserTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                callbackWithTweets.onSuccess(statusCode, tweet, frgName);
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
                callbackWithTweets.onSuccess(statusCode, tweets, frgName);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callbackWithTweets.onFailure(statusCode, responseString, throwable, frgName);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 429) {
                    if (retry > 0) {
                        Handler handler = new Handler();
                        Runnable runnableCode = () -> getUserTimeline(callbackWithTweets, uid, twitterHanele, sinceId, maxId, retry-1, 2 * delay, frgName);
                        handler.postDelayed(runnableCode, delay);
                        return;
                    }
                    callbackWithTweets.onFailure(statusCode, "Too many requests. Please wait a few minutes and try again", throwable, frgName);
                }
                List<TwitterErrors.TwitterError> errors = gson.fromJson(errorResponse.toString(), TwitterErrors.class).errors;
                callbackWithTweets.onFailure(statusCode, errors.get(0).errorMessage, throwable, frgName);
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
                callbackWithTweets.onFailure(statusCode, errorStrings, throwable, frgName);
            }
        }, uid, twitterHanele, sinceId, maxId);
    }

    private TwitterClient client = SimpleTwitterApplication.getRestClient();
    private Gson gson = new GsonBuilder().registerTypeAdapter(Tweet.class, new TweetJsonSerializer()).registerTypeAdapter(Tweet.class, new TweetJsonDeserializer()).create();

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets, long sinceId, long maxId, String frgName) {
        getTimeline("home", callbackWithTweets, sinceId, maxId, 3, 500, frgName);
    }

    public void getMentionsTimeline(final CallbackWithTweets callbackWithTweets, long sinceId, long maxId, String frgName) {
        getTimeline("mentions", callbackWithTweets, sinceId, maxId, 3, 500, frgName);
    }

    private void getTimeline(String which, final CallbackWithTweets callbackWithTweets, final long sinceId, final long maxId, final int retry, int delay, String frgName) {
        client.getTimeline(which, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                callbackWithTweets.onSuccess(statusCode, tweet, frgName);
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
                callbackWithTweets.onSuccess(statusCode, tweets, frgName);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                callbackWithTweets.onFailure(statusCode, responseString, throwable, frgName);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (statusCode == 429) {
                    if (retry > 0) {
                        Handler handler = new Handler();
                        Runnable runnableCode = () -> getTimeline(which, callbackWithTweets, sinceId, maxId, retry-1, 2 * delay, frgName);
                        handler.postDelayed(runnableCode, delay);
                        return;
                    }
                    callbackWithTweets.onFailure(statusCode, "Too many requests. Please wait a few minutes and try again", throwable, frgName);
                }
                List<TwitterErrors.TwitterError> errors = gson.fromJson(errorResponse.toString(), TwitterErrors.class).errors;
                callbackWithTweets.onFailure(statusCode, errors.get(0).errorMessage, throwable, frgName);
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
                callbackWithTweets.onFailure(statusCode, errorStrings, throwable, frgName);
            }
        }, sinceId, maxId);
    }

    public void postTweet(String text, CallbackWithTweets callbackWithTweets, String frgName) {
        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Tweet tweet = gson.fromJson(response.toString(), Tweet.class);
                callbackWithTweets.onSuccess(statusCode, tweet, frgName);

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

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets, long maxId, String frgName) {
        getHomeTimeline(callbackWithTweets, 1, maxId, frgName);
    }

    public void getMentionsTimeline(final CallbackWithTweets callbackWithTweets, long maxId, String frgName) {
        getMentionsTimeline(callbackWithTweets, 1, maxId, frgName);
    }

    public void getHomeTimeline(final CallbackWithTweets callbackWithTweets, String frgName) {
        getHomeTimeline(callbackWithTweets, 1, -1, frgName);
    }

    public void getMentionsTimeline(final CallbackWithTweets callbackWithTweets, String frgName) {
        getMentionsTimeline(callbackWithTweets, 1, -1, frgName);
    }

    public void getUserLookup(CallbackWithUsers callbackWithUsers, long uId, String search) {
        client.getSelf(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (uId <0 && search == null) {
                    callbackWithUsers.onSuccess(statusCode, gson.fromJson(response.toString(), User.class));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public interface CallbackWithTweets {
        void onSuccess(int statusCode, Tweet tweet, String dest);
        void onSuccess(int statusCode, List<Tweet> tweets, String dest);
        void onFailure(int statusCode, String errorString, Throwable throwable, String dest);
        void onFailure(int statusCode, List<String> errorStrings, Throwable throwable, String dest);
    }

    public interface CallbackWithUsers {
        void onSuccess(int statusCode, User user);
        void onSuccess(int statusCode, User user, long uId);
        void onSuccess(int statusCode, List<User> users, String search);
        void onFailure(int statusCode, String errorString, Throwable throwable, String uId);
        void onFailure(int statusCode, List<String> errorStrings, Throwable throwable, String Search);
    }
}
