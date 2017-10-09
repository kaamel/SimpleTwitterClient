package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaamel.simpletwitterclient.adapters.MainFragmentPagerAdapter;
import com.kaamel.simpletwitterclient.callbacks.MainActivityToTwitterFragmentsCallbacks;
import com.kaamel.simpletwitterclient.callbacks.OnFragmentInteractionListener;
import com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.User;

import org.parceler.Parcels;

import java.util.List;

public abstract class AbstarctBaseActivity extends AppCompatActivity
        implements ComposeTweetDialogFragment.OnTweetComposerUpdateListener,
        OnFragmentInteractionListener,
        TwitterClientHelper.CallbackWithTweets {

    private static final int SHOW_DETAIL_INTENT = 101;
    private SharedPreferences sharedPref;
    private final TwitterClientHelper twitterClientHelper = new TwitterClientHelper();

    private ViewPager viewPager;
    private MainFragmentPagerAdapter adapter;

    private ImageView logo;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private static User me;

    private void composeTweet(String body) {
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance(body, (body==null || body.length() ==0)?"New tweet":"Continue editing");
        dialog.show(getSupportFragmentManager(), body);
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
    public void onFragmentInteraction(Bundle bundle) {
        //this is an old callback that is not used in this app
        //// TODO: 10/7/17 remove it
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + getCurrentFragmentTag() + " clicked hastag " + hashtag, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserNameClicked(String name) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + getCurrentFragmentTag() + " clicked user name " + name, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserNameClicked(int position) {
        Tweet tweet = getFragmentCallback(getCurrentFragmentTag()).getTweet(position);
        onUserProfileClicked(tweet.getUser());
    }

    @Override
    public void onUserHandleClicked(String twitterHandle) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + getCurrentFragmentTag() + " clicked twitter handle: " + twitterHandle, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserHandleClicked(int position) {
        Tweet tweet = getFragmentCallback(getCurrentFragmentTag()).getTweet(position);
        onUserProfileClicked(tweet.getUser());
    }

    @Override
    public void onUserProfileClicked(int position) {
        onUserProfileClicked(getFragmentCallback(getCurrentFragmentTag()).getTweet(position).getUser());
    }

    @Override
    public void onUserProfileClicked(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        startActivityForResult(intent, SHOW_DETAIL_INTENT);
    }

    @Override
    public void onUserEmailClicked(User user) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + getCurrentFragmentTag() + " clicked email user " + user.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLikeClicked(Tweet tweet) {
        //// TODO: 10/7/17
        if (tweet != null && tweet.isFavorited())
            twitterClientHelper.postFavor(tweet.getId(), this, getCurrentFragmentTag());
        else if (tweet != null && !tweet.isFavorited())
            twitterClientHelper.postUnFavor(tweet.getId(), this, getCurrentFragmentTag());
    }

    @Override
    public void onTweetClicked(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(getFragmentCallback(getCurrentFragmentTag()).getTweet(position)));
        startActivityForResult(intent, SHOW_DETAIL_INTENT);
    }

    @Override
    public void getMessages(long maxId, String frgTag) {
        twitterClientHelper.getDirectMessages(this, maxId, frgTag);
    }

    @Override
    public void onEmailClicked(int position) {
        onUserEmailClicked(getFragmentCallback(getCurrentFragmentTag()).getTweet(position).getUser());
    }

    @Override
    public void onReplyClicked(int position) {
        String body = getFragmentCallback(getCurrentFragmentTag()).getTweet(position).getBody();
        String replyTo = getFragmentCallback(getCurrentFragmentTag()).getTweet(position).getUser().getTwitterHandle();
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance("@" + replyTo + " " + body, "Replying");
        dialog.show(getSupportFragmentManager(), body);
    }

    @Override
    public void onRetweetClicked(int position) {
        String body = getFragmentCallback(getCurrentFragmentTag()).getTweet(position).getBody();
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance("RT " + body, "Retweeting");
        dialog.show(getSupportFragmentManager(), body);
    }

    @Override
    public void onLikeClicked(int position) {
        onLikeClicked(getFragmentCallback(getCurrentFragmentTag()).getTweet(position));
    }

    @Override
    public void getHomeTweets(long maxId, String frgTag) {
        twitterClientHelper.getHomeTimeline(this, maxId, frgTag);
    }

    @Override
    public void getUserTweets(long uid, String twitterHanle, long maxId, @NonNull String src) {
        twitterClientHelper.getUserTimeline(this, uid, twitterHanle, maxId, src);
    }

    @Override
    public void getMentions(long maxId, String frgTag) {
        twitterClientHelper.getMentionsTimeline(this, maxId, frgTag);
    }

    @Override
    public void onSuccess(int statusCode, Tweet tweet, String des) {
        getFragmentCallback(des).onReceivedTweet(tweet);
    }

    @Override
    public void onSuccess(int statusCode, List<Tweet> ts, String des) {
        getFragmentCallback(des).onReceivedTweets(ts);
    }

    @Override
    public void onFailure(int statusCode, String responseString, Throwable throwable, String des) {
        getFragmentCallback(des).onClientError(responseString);
    }

    @Override
    public void onFailure(int statusCode, List<String> errorStrings, Throwable throwable, String des) {
        getFragmentCallback(des).onClientErrors(errorStrings);
    }

    public MainActivityToTwitterFragmentsCallbacks getFragmentCallback(String tag) {
        return (MainActivityToTwitterFragmentsCallbacks) getSupportFragmentManager().findFragmentByTag(tag);
    }

    abstract String getCurrentFragmentTag();

    abstract void setCurrentFragmentTag(String tag);
}
