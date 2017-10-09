package com.kaamel.simpletwitterclient.activities;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kaamel.simpletwitterclient.fragments.TweetsHomeListFragment;
import com.kaamel.simpletwitterclient.fragments.TweetsMentionsListFragment;

/**
 * Created by kaamel on 10/5/17.
 */

class MainFragmentPagerAdapter extends FragmentPagerAdapter {
    //final int PAGE_COUNT = 4;
    //private String tabTitles[] = new String[] { "Home", "Search", "Notifications", "Messages" };
    //final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Home", "Mentions" };
    //private String tabTitles[] = new String[] { "Notifications" };
    private Context context;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return TweetsMentionsListFragment.newInstance("", tabTitles[position]);
            case 0:
            default:
                return TweetsHomeListFragment.newInstance("", tabTitles[position]);
        }
        //return TweetsMessagesListFragment.newInstance("", tabTitles[position]);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public int getPosition(String tag) {
        for (int i=0; i<tabTitles.length; i++) {
            if (tabTitles[i].equals(tag))
                return i;
        }
        return -1;
    }

    public String getTabName(int position) {
        if (position < 0 || position >= tabTitles.length)
            return tabTitles[0];
        return tabTitles[position];
    }
}

