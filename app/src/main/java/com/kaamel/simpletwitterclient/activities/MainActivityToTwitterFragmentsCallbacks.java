package com.kaamel.simpletwitterclient.activities;

import com.kaamel.simpletwitterclient.twitteritems.Tweet;

import java.util.List;

/**
 * Created by kaamel on 10/6/17.
 */

public interface MainActivityToTwitterFragmentsCallbacks {
    void onReceivedTweet(Tweet tweet);
    void onReceivedTweets(List<Tweet> tweets);
    void onClientError(String message);
    void onClientErrors(List<String> messages);

    Tweet getTweet(int position);
}
