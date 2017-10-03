package com.kaamel.simpletwitterclient.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.activities.TimelineActivity;
import com.kaamel.simpletwitterclient.databinding.ItemTweetBinding;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.TwitterMedia;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;
import com.kaamel.simpletwitterclient.utils.Utils;
import com.kaamel.simpletwitterclient.views.PatternEditableBuilder;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by kaamel on 9/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private List<Tweet> tweets;
    private Context context;
    MediaController mediaController;

    public TweetAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        mediaController= new MediaController(context);
        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tweet tweet = tweets.get(position);
        holder.tvBody.setText(tweet.body);
        holder.tvBody.setTag("notclicked");
        new PatternEditableBuilder().
                addPattern(Pattern.compile("#(\\w+)"), Color.BLUE,
                        text -> {
                            ((TimelineActivity) context).onHashTagClicked(text);
                            holder.tvBody.setTag("clicked");
                }).into(holder.tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        text -> {
                            ((TimelineActivity) context).onUserClicked(text);
                            holder.tvBody.setTag("clicked");
                        }).into(holder.tvBody);

        String name = tweet.user.name == null?"":tweet.user.name;
        holder.tvUserName.setText(name);
        String twitterHnadle = tweet.user.twitterHandle == null?"":("@" + tweet.user.twitterHandle);
        holder.tvTwitterHandle.setText(twitterHnadle);
        holder.tvCreatedAt.setText(Utils.twitterTimeToDiffFromNow(tweet.createdAt));
        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .placeholder(R.drawable.ic_action_twitter)
                .error(android.R.drawable.stat_notify_error)
                .bitmapTransform(new RoundedCornersTransformation( context, 37, 2))
                .into(holder.ivProfileImage);

        if (tweet.entities != null && tweet.entities.medias != null) {
            List<TwitterMedia> medias = tweet.entities.medias;
            if (medias.get(0) != null && "photo".equals(medias.get(0).type)) {
                Glide.with(context)
                            .load(medias.get(0).url)
                            .placeholder(R.drawable.ic_action_twitter)
                            .error(android.R.drawable.stat_notify_error)
                            .bitmapTransform(new RoundedCornersTransformation(context, 37, 2))
                            .into(holder.ivEmbedded);
                holder.vvEmbedded.setVisibility(View.GONE);
                holder.ivEmbedded.setVisibility(View.VISIBLE);
            }
            else if (medias.get(0) != null && "animated_gif".equals(medias.get(0).type) ||
                    medias.get(0) != null && "video".equals(medias.get(0).type)) {
                mediaController.setAnchorView(holder.vvEmbedded);
                holder.vvEmbedded.setMediaController(mediaController);
                if (tweet.entities.medias.get(0).videoInfo != null && tweet.entities.medias.get(0).videoInfo.variants != null &&
                        tweet.entities.medias.get(0).videoInfo.variants.get(0) != null)
                holder.vvEmbedded.setVideoURI(Uri.parse(tweet.entities.medias.get(0).videoInfo.variants.get(0).url));
                holder.vvEmbedded.requestFocus();
                holder.vvEmbedded.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //mp.start();
                    }
                });
                //holder.vvEmbedded.start();
                holder.vvEmbedded.setVisibility(View.VISIBLE);
                holder.ivEmbedded.setVisibility(View.GONE);
            }
            else {
                holder.vvEmbedded.setVisibility(View.GONE);
                holder.ivEmbedded.setVisibility(View.GONE);
            }
        }
        else {
            holder.vvEmbedded.setVisibility(View.GONE);
            holder.ivEmbedded.setVisibility(View.GONE);
        }

        holder.binding.executePendingBindings();   // update the view now
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        final ItemTweetBinding binding;  // this will be used by onBindViewHolder()

        TextView tvBody;
        TextView tvTwitterHandle;
        TextView tvUserName;
        TextView tvCreatedAt;
        ImageView ivProfileImage;
        ImageView ivEmbedded;
        VideoView vvEmbedded;
        ImageButton ibReply;
        ImageButton ibRetweet;
        ImageButton ibLike;
        ImageButton ibEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            tvBody = binding.tvBody;
            tvUserName = binding.tvUserName;
            tvCreatedAt = binding.tvCreatedAt;
            tvTwitterHandle = binding.tvTwitterHandle;
            ivProfileImage = binding.ivProfileImage;
            ivEmbedded = binding.ivEmbedded;
            vvEmbedded = binding.vvEmbedded;
            ibEmail = binding.ibEmail;
            ibLike = binding.ibLike;
            ibReply = binding.ibReply;
            ibRetweet = binding.ibRetweet;

            ibEmail.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), "Email is clicked", Toast.LENGTH_LONG).show();
            });

            ibReply.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), "Reply is clicked", Toast.LENGTH_LONG).show();
            });

            ibRetweet.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), "Retweet is clicked", Toast.LENGTH_LONG).show();
            });

            ibLike.setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(), "Like is clicked", Toast.LENGTH_LONG).show();
            });

            View.OnClickListener detailViewListener = v -> {
                final int position = getAdapterPosition();
                ((TimelineActivity) itemView.getContext()).onTweetClicked(position);
            };

            View.OnClickListener userListener = v -> {
                final int position = getAdapterPosition();
                ((TimelineActivity) itemView.getContext()).onUserClicked(position);
            };

            tvCreatedAt.setOnClickListener(detailViewListener);
            tvUserName.setOnClickListener(userListener);
            tvTwitterHandle.setOnClickListener(userListener);
            ivProfileImage.setOnClickListener(userListener);
            ivEmbedded.setOnClickListener(v -> {
                //// TODO: 10/1/17 open in chrome
            });
            tvBody.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                String tag = (String) tvBody.getTag();
                if (tag != null && tag.equals("clicked")) {
                    tvBody.setTag("notclicked");
                    return;
                }
                ((TimelineActivity) itemView.getContext()).onTweetClicked(position);
            });
        }
    }
}
