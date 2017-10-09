package com.kaamel.simpletwitterclient.callbacks;

/**
 * Created by kaamel on 10/3/17.
 */

public interface TweetsListInterface {

    public void onTweetClicked(int position);

    public void onUserClicked(int position);

    public void onHashTagClicked(String hastag);

    public void onUserClicked(String text);
}
