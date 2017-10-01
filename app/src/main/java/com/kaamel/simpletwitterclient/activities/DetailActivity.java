package com.kaamel.simpletwitterclient.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.ActivityDetailBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;
import com.kaamel.simpletwitterclient.utils.Utils;


public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding binding;
    TextView tvBody;
    TextView tvUserName;
    TextView tvTwitterHandle;
    TextView tvCreatedAt;
    ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Tweet tweet = getIntent().getParcelableExtra("tweet");
        tvBody = binding.tvBody;
        tvUserName = binding.tvUserName;
        tvTwitterHandle = binding.tvTwitterHandle;
        tvCreatedAt = binding.tvCreatedAt;
        ivProfileImage = binding.ivProfileImage;

        tvBody.setText(tweet.body);
        String name = tweet.user.name == null?"":tweet.user.name;
        tvUserName.setText(name);
        String twitterHnadle = tweet.user.twitterHandle == null?"":("@" + tweet.user.twitterHandle);
        tvTwitterHandle.setText(twitterHnadle);
        tvCreatedAt.setText(Utils.twitterTimeToDiffFromNow(tweet.createdAt));
        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_action_twitter)
                .error(android.R.drawable.stat_notify_error)
                .bitmapTransform(new RoundedCornersTransformation(this, 37, 2))
                .into(ivProfileImage);
    }
}
