package com.kaamel.simpletwitterclient.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.adapters.MainFragmentPagerAdapter;
import com.kaamel.simpletwitterclient.callbacks.MainActivityToTwitterFragmentsCallbacks;
import com.kaamel.simpletwitterclient.callbacks.OnFragmentInteractionListener;
import com.kaamel.simpletwitterclient.databinding.ActivityMainBinding;
import com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment;
import com.kaamel.simpletwitterclient.models.TwitterClientHelper;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.User;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ComposeTweetDialogFragment.OnTweetComposerUpdateListener,
        OnFragmentInteractionListener,
        TwitterClientHelper.CallbackWithTweets {

    private static final int SHOW_DETAIL_INTENT = 101;
    private static String currentFragmentTag;
    private SharedPreferences sharedPref;
    private final TwitterClientHelper twitterClientHelper = new TwitterClientHelper();

    private ViewPager viewPager;
    private MainFragmentPagerAdapter adapter;

    private ImageView logo;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private static User me;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        toolbar = binding.appBar.toolbar;
        setSupportActionBar(toolbar);
        logo = getLogoView(toolbar);

        sharedPref = getSharedPreferences(
                /*getString(R.string.preference_file_key)*/ "simpletwitterclient", Context.MODE_PRIVATE);

        FloatingActionButton fab = binding.appBar.fab;
        fab.setOnClickListener(view ->
                composeTweet(restoreTweet()));

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setToolbarNavigationClickListener(view -> drawer.openDrawer(GravityCompat.START));

        toolbar.setNavigationIcon(R.drawable.ic_twitter_logo_blue_48);

        ActionBar sab = getSupportActionBar();
        sab.setDisplayShowHomeEnabled(true);
        //sab.setLogo(R.drawable.ic_twitter_logo_blue_48);
        //sab.setTitle("Home");
        sab.setDisplayUseLogoEnabled(true);
        sab.setDisplayShowTitleEnabled(true);

        toolbar.setTitleTextColor(Color.BLACK);
        toggle.syncState();

        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = binding.appBar.contentMain.viewpager;
        adapter = new MainFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = binding.appBar.contentMain.slidingTabs;
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFragmentTag = (adapter.getTabName(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //currentFragmentTag = null;
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                currentFragmentTag = (adapter.getTabName(tab.getPosition()));
            }
        });

        if (currentFragmentTag == null) {
            goToPageByPosition(0);
        }
        else {
            int postition = adapter.getPosition(currentFragmentTag);
            viewPager.setCurrentItem(postition);
        }

        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Tweet tweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));
                String action = intent.getStringExtra("action");
                String data = intent.getStringExtra("data");
                if ("user_handle".equals(action)) {
                    if (data != null)
                        onUserHandleClicked(data);
                    else
                        onUserProfileClicked(tweet.getUser());
                }
                else if ("user_name".equals(action)) {
                    if (data != null)
                        onUserNameClicked(data);
                    else
                        onUserProfileClicked(tweet.getUser());
                }
                else if ("user_profile".equals(action)) {
                    onUserProfileClicked(tweet.getUser());
                }
                else if ("hashtag".equals(action)) {
                    onHashtagClicked(data);
                }
                else if ("email".equals(action)) {
                    onUserEmailClicked(tweet.getUser());
                }
                else if ("like".equals(action))
                    onLikeClicked(tweet);
                else
                    Toast.makeText(context, "Unkonw action: " + action + "/" + data, Toast.LENGTH_SHORT).show();
            }
        };
        IntentFilter filter = new IntentFilter("com.kaamel.DETAIL_ACTIVTY_NOTIFICATION");
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);

        twitterClientHelper.getUserLookup(new TwitterClientHelper.CallbackWithUsers() {
            @Override
            public void onSuccess(int statusCode, User user) {
                me = user;

                //View hView =  navigationView.inflateHeaderView(R.layout.nav_header_main);
                //ImageView imgvw = (ImageView)hView.findViewById(R.id.imageView);
                //Glide.with(getApplicationContext())
                //        .load(user.getProfileImageUrl())
                //        .bitmapTransform(new RoundedCornersTransformation( getApplicationContext(), 37, 2))
                //        .into(imgvw);

                getSupportActionBar().setTitle(user.getName());
                Glide.with(getApplicationContext())
                        .load(user.getProfileImageUrl())
                        .bitmapTransform(new RoundedCornersTransformation( getApplicationContext(), 37, 2))
                        .into(getLogoView(toolbar));
                getSupportActionBar().setTitle(user.getName());

                Glide.with(getApplicationContext())
                        .load(user.getProfileImageUrl())
                        .bitmapTransform(new RoundedCornersTransformation( getApplicationContext(), 37, 2))
                        .into((ImageView) drawer.findViewById(R.id.imageView));
                ((TextView) drawer.findViewById(R.id.textView)).setText("@" + user.getTwitterHandle());
                ((TextView) drawer.findViewById(R.id.first_line)).setText(user.getName());
                getSupportActionBar().setTitle(user.getName());
            }

            @Override
            public void onSuccess(int statusCode, User user, long uId) {

            }

            @Override
            public void onSuccess(int statusCode, List<User> users, String search) {

            }

            @Override
            public void onFailure(int statusCode, String errorString, Throwable throwable, String uId) {

            }

            @Override
            public void onFailure(int statusCode, List<String> errorStrings, Throwable throwable, String Search) {

            }
        }, -1, null);

        Intent intent = getIntent();
        if (intent != null) {
            processImplicitIntent(intent);
        }
    }

    private void goToPageByPosition(int position) {
        if (position<0 && position > adapter.getCount() -1)
            position = 0;
        currentFragmentTag = adapter.getTabName(position);
        viewPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user_profile) {
            if (me != null) {
                onUserProfileClicked(me);
                return true;
            }
            twitterClientHelper.getUserLookup(new TwitterClientHelper.CallbackWithUsers() {
                @Override
                public void onSuccess(int statusCode, User user) {
                    me = user;
                    onUserProfileClicked(user);
                }

                @Override
                public void onSuccess(int statusCode, User user, long uId) {

                }

                @Override
                public void onSuccess(int statusCode, List<User> users, String search) {

                }

                @Override
                public void onFailure(int statusCode, String errorString, Throwable throwable, String uId) {

                }

                @Override
                public void onFailure(int statusCode, List<String> errorStrings, Throwable throwable, String Search) {

                }
            }, -1, null);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        Toast.makeText(this, "fragment " + currentFragmentTag + " clicked hastag " + hashtag, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserNameClicked(String name) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + currentFragmentTag + " clicked user name " + name, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserNameClicked(int position) {
        Tweet tweet = getFragmentCallback(currentFragmentTag).getTweet(position);
        onUserProfileClicked(tweet.getUser());
    }

    @Override
    public void onUserHandleClicked(String twitterHandle) {
        //// TODO: 10/7/17
        Toast.makeText(this, "fragment " + currentFragmentTag + " clicked twitter handle: " + twitterHandle, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUserHandleClicked(int position) {
        Tweet tweet = getFragmentCallback(currentFragmentTag).getTweet(position);
        onUserProfileClicked(tweet.getUser());
    }

    @Override
    public void onUserProfileClicked(int position) {
        onUserProfileClicked(getFragmentCallback(currentFragmentTag).getTweet(position).getUser());
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
        Toast.makeText(this, "fragment " + currentFragmentTag + " clicked email user " + user.getName(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLikeClicked(Tweet tweet) {
        if (tweet != null && tweet.isFavorited())
            twitterClientHelper.postUnFavor(tweet.getId(), this, currentFragmentTag);
        else if (tweet != null && !tweet.isFavorited())
            twitterClientHelper.postFavor(tweet.getId(), this, currentFragmentTag);
    }

    @Override
    public void onTweetClicked(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("tweet", Parcels.wrap(getFragmentCallback(currentFragmentTag).getTweet(position)));
        startActivityForResult(intent, SHOW_DETAIL_INTENT);
    }

    @Override
    public void onEmailClicked(int position) {
        onUserEmailClicked(getFragmentCallback(currentFragmentTag).getTweet(position).getUser());
    }

    @Override
    public void onReplyClicked(int position) {
        String body = getFragmentCallback(currentFragmentTag).getTweet(position).getBody();
        String replyTo = getFragmentCallback(currentFragmentTag).getTweet(position).getUser().getTwitterHandle();
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance("@" + replyTo + " " + body, "Replying");
        dialog.show(getSupportFragmentManager(), body);
    }

    @Override
    public void onRetweetClicked(int position) {
        String body = getFragmentCallback(currentFragmentTag).getTweet(position).getBody();
        DialogFragment dialog = ComposeTweetDialogFragment.newInstance("RT " + body, "Retweeting");
        dialog.show(getSupportFragmentManager(), body);
    }

    @Override
    public void onLikeClicked(int position) {
        onLikeClicked(getFragmentCallback(currentFragmentTag).getTweet(position));
    }

    public void getMessages(long maxId, String frgTag) {
        twitterClientHelper.getDirectMessages(this, maxId, frgTag);
    }

    @Override
    public void searchTweets(String srch, long maxId, @NonNull String sr) {
        twitterClientHelper.searchTweets(this, srch, maxId, sr);
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
    public void onUpdate(int status, String body) {
        if (status == STATUS_TWEET) {
            goToPageByPosition(0);
            twitterClientHelper.postTweet(body, this, adapter.getTabName(0));
        }

        else if (status == STATUS_SAVE)
            saveTweet(body);
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
        int fragmentPosition = adapter.getPosition(tag);
        return (MainActivityToTwitterFragmentsCallbacks) adapter.instantiateItem(viewPager, fragmentPosition);
    }

    private Fragment getFragmentByTag(String tag) {
        int fragmentPosition = adapter.getPosition(tag);
        return (Fragment) adapter.instantiateItem(viewPager, fragmentPosition);
    }

    private ImageView getLogoView(Toolbar toolbar) {
        for (int i = 0; i < toolbar.getChildCount(); i++)
            if(toolbar.getChildAt(i) instanceof ImageView)
                return (ImageView) toolbar.getChildAt(i);

        return null;
    }
}
