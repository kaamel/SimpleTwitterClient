package com.kaamel.simpletwitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.ActivityProfileBinding;
import com.kaamel.simpletwitterclient.twitteritems.User;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;

import org.parceler.Parcels;

public class ProfileActivity extends AppCompatActivity {

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
        collapsingToolbar = binding.toolbarLayout;
        tvTwetterhandle = binding.layout.tvUserHandle;
        tvFollowers = binding.layout.tvUserFollowers;
        tvFollowing = binding.layout.tvUserFollowing;
        tvDescription = binding.layout.tvDescription;


        collapsingToolbar.setContentScrimColor(0);
        collapsingToolbar.setTitle(user.getName());
        collapsingToolbar.setBackgroundColor(getResources().getColor(R.color.accent_material_light_twitter));
        //collapsingToolbar.setExpandedTitleColor(Color.BLACK);
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
    }
}
