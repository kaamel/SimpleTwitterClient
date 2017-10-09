package com.kaamel.simpletwitterclient.callbacks;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.User;

/**
 * Created by kaamel on 10/6/17.
 */

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Bundle bundle);

    void onHashtagClicked(String hashtag);

    public void onUserNameClicked(String name);
    public void onUserNameClicked(int position);
    public void onUserHandleClicked(String twitterHandle);
    public void onUserHandleClicked(int position);
    public void onUserProfileClicked(int position);
    public void onUserProfileClicked(User user);
    public void onUserEmailClicked(User user);
    public void onLikeClicked(Tweet tweet);

    public void onTweetClicked(int position);

    public void onEmailClicked(int position);
    public void onReplyClicked(int position);
    public void onRetweetClicked(int position);
    public void onLikeClicked(int position);

    public void getHomeTweets(long maxId, @NonNull String src);
    public void getUserTweets(long uid, String twitterHanle, long maxId, @NonNull String src);
    public void getMentions(long maxId, @NonNull String sr);
    public void getMessages(long maxId, @NonNull String sr);

    public void searchTweets(String srch, long maxId, @NonNull String sr);
}
