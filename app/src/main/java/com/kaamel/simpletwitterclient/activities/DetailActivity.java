package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.ActivityDetailBinding;
import com.kaamel.simpletwitterclient.fragments.ComposeTweetDialogFragment;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.TwitterMedia;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;
import com.kaamel.simpletwitterclient.utils.Utils;

import java.util.List;


public class DetailActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnTweetComposerUpdateListener {

    ActivityDetailBinding binding;
    TextView tvBody;
    TextView tvUserName;
    TextView tvTwitterHandle;
    TextView tvCreatedAt;
    ImageView ivProfileImage;
    ImageButton ibReply;
    ImageButton ibRetweet;

    ImageView ivEmbedded;
    VideoView vvEmbedded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Tweet tweet = getIntent().getParcelableExtra("tweet");

        Toolbar myToolbar = binding.toolbar;
        setSupportActionBar(myToolbar);

        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
            sab.setLogo(R.mipmap.ic_launcher_blue);
            sab.setTitle(tweet.user.name);
            sab.setDisplayUseLogoEnabled(false);
            sab.setDisplayShowTitleEnabled(true);

            myToolbar.setNavigationOnClickListener(v -> {
                onBackPressed();
            });
        }

        tvBody = binding.tvBody;
        tvUserName = binding.tvUserName;
        tvTwitterHandle = binding.tvTwitterHandle;
        tvCreatedAt = binding.tvCreatedAt;
        ivProfileImage = binding.ivProfileImage;
        ibReply = binding.ibReply;
        ibRetweet = binding.ibRetweet;

        ivEmbedded = binding.ivEmbedded;
        vvEmbedded = binding.vvEmbedded;

        String body = tweet.body;
        String replyTo = tweet.user.twitterHandle;
        ibReply.setOnClickListener(v -> {
            DialogFragment dialog = ComposeTweetDialogFragment.newInstance("@" + replyTo + " " + body, "Replying");
            dialog.show(getSupportFragmentManager(), body);
        });

        ibRetweet.setOnClickListener(v -> {
            DialogFragment dialog = ComposeTweetDialogFragment.newInstance("RT " + body, "Retweeting");
            dialog.show(getSupportFragmentManager(), body);
        });

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

        MediaController mediaController = new MediaController(this);
        if (tweet.entities != null && tweet.entities.medias != null) {
            List<TwitterMedia> medias = tweet.entities.medias;
            if (medias.get(0) != null && "photo".equals(medias.get(0).type)) {
                Glide.with(this)
                        .load(medias.get(0).url)
                        .placeholder(R.drawable.ic_action_twitter)
                        .error(android.R.drawable.stat_notify_error)
                        .bitmapTransform(new RoundedCornersTransformation(this, 37, 2))
                        .into(ivEmbedded);
                vvEmbedded.setVisibility(View.GONE);
                ivEmbedded.setVisibility(View.VISIBLE);
            }
            else if (medias.get(0) != null && "animated_gif".equals(medias.get(0).type) ||
                    medias.get(0) != null && "video".equals(medias.get(0).type)) {
                mediaController.setAnchorView(vvEmbedded);
                vvEmbedded.setMediaController(mediaController);
                if (tweet.entities.medias.get(0).videoInfo != null && tweet.entities.medias.get(0).videoInfo.variants != null &&
                        tweet.entities.medias.get(0).videoInfo.variants.get(0) != null)
                    vvEmbedded.setVideoURI(Uri.parse(tweet.entities.medias.get(0).videoInfo.variants.get(0).url));
                vvEmbedded.requestFocus();
                vvEmbedded.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //mp.start();
                    }
                });
                //holder.vvEmbedded.start();
                vvEmbedded.setVisibility(View.VISIBLE);
                ivEmbedded.setVisibility(View.GONE);
            }
            else {
                vvEmbedded.setVisibility(View.GONE);
                ivEmbedded.setVisibility(View.GONE);
            }
        }
        else {
            vvEmbedded.setVisibility(View.GONE);
            ivEmbedded.setVisibility(View.GONE);
        }
    }

    @Override
    public void onUpdate(int status, String body) {
        if (status == ComposeTweetDialogFragment.OnTweetComposerUpdateListener.STATUS_CANCEL)
            return;
        if (body != null) {
            if (body.trim().length() > 0) {
                Intent intent = new Intent();
                intent.putExtra("body", body.trim());
                intent.putExtra("status", status);
                setResult(RESULT_OK, intent);
            }
        }
        finish();
    }
}
