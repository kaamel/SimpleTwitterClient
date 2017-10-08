package com.kaamel.simpletwitterclient.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.ActivityProfileBinding;
import com.kaamel.simpletwitterclient.fragments.TweetsUserListFragment;
import com.kaamel.simpletwitterclient.twitteritems.User;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

public class ProfileActivity extends AbstarctBaseActivity {

    ActivityProfileBinding binding;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    ImageView ivProfileImage;
    TextView tvTwetterhandle;
    TextView tvFollowers;
    TextView tvFollowing;
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);

        User user = Parcels.unwrap(getIntent().getParcelableExtra("user"));

        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColorString()));
        collapsingToolbar = binding.toolbarLayout;
        tvTwetterhandle = binding.layout.tvUserHandle;
        tvFollowers = binding.layout.tvUserFollowers;
        tvFollowing = binding.layout.tvUserFollowing;
        tvDescription = binding.layout.tvDescription;

        collapsingToolbar.setContentScrimColor(0);
        collapsingToolbar.setTitle(user.getName());
        //collapsingToolbar.setBackgroundColor(Color.parseColor("#" + user.getProfileBackgroundColorString()));
        collapsingToolbar.setExpandedTitleColor(Color.BLACK);
        ivProfileImage = binding.ivProfileImage;
        tvTwetterhandle.setText("@" + user.getTwitterHandle());

        int fr = user.getFollowersCount();
        int fl = user.getFriendsCount();
        String desc = user.getDescription();
        tvFollowing.setText(String.valueOf(user.getFriendsCount()));
        tvFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvDescription.setText(String.valueOf(user.getDescription()));

        Glide.with(this)
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.ic_action_twitter)
                .error(android.R.drawable.stat_notify_error)
                .bitmapTransform(new RoundedCornersTransformation( this, 37, 2))
                .into(ivProfileImage);

        FragmentManager fm = getSupportFragmentManager();
        TweetsUserListFragment fragment = TweetsUserListFragment.newInstance(user.getUid(), user.getTwitterHandle(), getCurrentFragmentTag());
        fm.beginTransaction().add(R.id.flUserTweetsContainer, fragment, getCurrentFragmentTag()).commit();
    }

    @Override
    public void onUpdate(int status, String body) {

    }

    @Override
    String getCurrentFragmentTag() {
        return "ProfileActivity";
    }

    void setCurrentFragmentTag(String tag) {
    }
}
