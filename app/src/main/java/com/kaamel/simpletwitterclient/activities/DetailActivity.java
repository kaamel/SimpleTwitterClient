package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
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
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;
import com.kaamel.simpletwitterclient.utils.Utils;
import com.kaamel.simpletwitterclient.views.PatternEditableBuilder;

import org.parceler.Parcels;

import java.util.regex.Pattern;


public class DetailActivity extends AppCompatActivity implements
        ComposeTweetDialogFragment.OnTweetComposerUpdateListener {

    ActivityDetailBinding binding;
    TextView tvBody;
    TextView tvUserName;
    TextView tvTwitterHandle;
    TextView tvCreatedAt;
    ImageView ivProfileImage;
    ImageButton ibReply;
    TextView tvReplyCount;
    ImageButton ibRetweet;
    TextView tvRetweetCount;
    ImageButton ibLike;
    TextView tvLikeCount;
    ImageButton ibEmail;

    ImageView ivEmbedded;
    VideoView vvEmbedded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet"));
        Toolbar myToolbar = binding.toolbar;
        setSupportActionBar(myToolbar);

        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
            sab.setLogo(R.mipmap.ic_launcher_blue);
            sab.setTitle(tweet.getUser().getName());
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
        ibReply = binding.layout.ibReply;
        tvReplyCount = binding.layout.tvReplyCount;
        ibRetweet = binding.layout.ibRetweet;
        tvRetweetCount = binding.layout.tvRetwetCount;
        ibLike = binding.layout.ibLike;
        tvLikeCount = binding.layout.tvLikeCount;
        ibEmail = binding.layout.ibEmail;



        ivEmbedded = binding.ivEmbedded;
        vvEmbedded = binding.vvEmbedded;

        String body = tweet.getBody();
        String replyTo = tweet.getUser().getTwitterHandle();

        ibReply.setOnClickListener(v -> {
            DialogFragment dialog = ComposeTweetDialogFragment.newInstance("@" + replyTo + " " + body, "Replying");
            dialog.show(getSupportFragmentManager(), body);
        });

        ibRetweet.setOnClickListener(v -> {
            DialogFragment dialog = ComposeTweetDialogFragment.newInstance("RT " + body, "Retweeting");
            dialog.show(getSupportFragmentManager(), body);
        });

        ibLike.setOnClickListener(v -> {
            //Toast.makeText(this, "I like this tweet", Toast.LENGTH_LONG).show();
            sendToMainActivity(tweet, "like", null);
        });

        ibEmail.setOnClickListener(v -> {
            //Toast.makeText(this, "I am emailing to " + tweet.getUser().getName(), Toast.LENGTH_LONG).show();
            sendToMainActivity(tweet, "email", null);
        });

        tvBody.setText(body);
        new PatternEditableBuilder().
                addPattern(Pattern.compile("#(\\w+)"), Color.BLUE,
                        text -> {
                            sendToMainActivity(tweet, "hashtag", text);
                        }).into(tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        text ->  {
                            sendToMainActivity(tweet, "user_handle", text);
                }).into(tvBody);

        View.OnClickListener userNameListener = v -> {
            sendToMainActivity(tweet, "user_name", null);
        };

        View.OnClickListener userProfileListener = v -> {
            sendToMainActivity(tweet, "user_profile", tweet.getUser().getName());
        };

        View.OnClickListener userHandleListener = v -> {
            sendToMainActivity(tweet, "user_handle", null);
        };

        View.OnClickListener userLikeListener = v -> {
            sendToMainActivity(tweet, "like", tweet.getUser().getName());
        };

        View.OnClickListener userEmailListener = v -> {
            sendToMainActivity(tweet, "email", tweet.getUser().getName());
        };

        String name = tweet.getUser().getName() == null?"": tweet.getUser().getName();
        tvUserName.setText(name);
        tvUserName.setOnClickListener(userNameListener);
        String twitterHnadle = tweet.getUser().getTwitterHandle() == null?"":("@" + tweet.getUser().getTwitterHandle());
        tvTwitterHandle.setText(twitterHnadle);
        tvTwitterHandle.setOnClickListener(userHandleListener);
        tvCreatedAt.setText(Utils.twitterTimeToDiffFromNow(tweet.getCreatedAt()));
        Glide.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_action_twitter)
                .error(android.R.drawable.stat_notify_error)
                .bitmapTransform(new RoundedCornersTransformation(this, 37, 2))
                .into(ivProfileImage);
        ivProfileImage.setOnClickListener(userProfileListener);
        MediaController mediaController = new MediaController(this);

        if ("photo".equals(tweet.getMediaType())) {
            Glide.with(this)
                    .load(tweet.getMediaUrl())
                    .placeholder(R.drawable.ic_action_twitter)
                    .error(android.R.drawable.stat_notify_error)
                    .bitmapTransform(new RoundedCornersTransformation(this, 37, 2))
                    .into(ivEmbedded);
            vvEmbedded.setVisibility(View.GONE);
            ivEmbedded.setVisibility(View.VISIBLE);
        }
        else if ("video".equals(tweet.getMediaType()) || "animated_gif".equals(tweet.getMediaType())) {
            mediaController.setAnchorView(vvEmbedded);
            vvEmbedded.setMediaController(mediaController);
//            if (tweet.videoUrl != null)
                vvEmbedded.setVideoURI(Uri.parse(tweet.getVideoUrl()));
//            else
//                vvEmbedded.setVideoURI(Uri.parse(tweet.mediaType));
            vvEmbedded.requestFocus();
            vvEmbedded.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
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

        if (tweet.getReplyCount() >0 )
            tvReplyCount.setText(String.valueOf(tweet.getReplyCount()));
        if (tweet.getRetweetCount() > 0)
            tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        if (tweet.getFavoritCount() > 0)
            tvLikeCount.setText(String.valueOf(tweet.getFavoritCount()));
        if (tweet.isFavorited())
            ibLike.setImageResource(R.drawable.ic_action_twitter_like_red);
        ibLike.setOnClickListener(userLikeListener);
        ibEmail.setOnClickListener(userEmailListener);
    }

    @Override
    public void onUpdate(int status, String body) {
        if (status == ComposeTweetDialogFragment.OnTweetComposerUpdateListener.STATUS_CANCEL)
            return;
        if (body != null) {
            if (body.trim().length() > 0) {
                Intent intent = new Intent();
                intent.putExtra("request", "edit");
                intent.putExtra("body", body.trim());
                intent.putExtra("status", status);
                setResult(RESULT_OK, intent);
            }
        }
        finish();
    }

    void sendToMainActivity(Tweet tweet, String action, String data) {
        Intent intent = new Intent();
        intent.setAction("com.kaamel.DETAIL_ACTIVTY_NOTIFICATION");
        intent.putExtra("tweet", Parcels.wrap(tweet));
        intent.putExtra("action", action);
        if (data != null)
            intent.putExtra("data", data);
        sendBroadcast(intent);
    }
}
