package com.kaamel.simpletwitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.activities.TimelineActivity;
import com.kaamel.simpletwitterclient.databinding.ItemTweetBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.models.TwitterMedia;
import com.kaamel.simpletwitterclient.utils.RoundedCornersTransformation;
import com.kaamel.simpletwitterclient.utils.Utils;

import java.util.List;

/**
 * Created by kaamel on 9/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder>{

    private List<Tweet> tweets;
    private Context context;

    public TweetAdapter(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);

        return new ViewHolder(tweetView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Tweet tweet = tweets.get(position);
        holder.tvBody.setText(tweet.body);
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
                holder.ivEmbedded.setVisibility(View.VISIBLE);
            }
            else {
                holder.ivEmbedded.setVisibility(View.GONE);
                if (medias.get(0) != null && "video".equals(medias.get(0).type)) {
                    Log.d("SimpleTweeterClient", "it is a video: " + medias.get(0).url);
                }
            }
        }
        else {
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

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            tvBody = binding.tvBody;
            tvUserName = binding.tvUserName;
            tvCreatedAt = binding.tvCreatedAt;
            tvTwitterHandle = binding.tvTwitterHandle;
            ivProfileImage = binding.ivProfileImage;
            ivEmbedded = binding.ivEmbedded;

            View.OnClickListener listener = v -> {
                final int position = getAdapterPosition();
                ((TimelineActivity) itemView.getContext()).onTweetClicked(position);
            };
            itemView.setOnClickListener(listener);
            tvBody.setOnClickListener(listener);
            ivEmbedded.setOnClickListener(v -> {
                //// TODO: 10/1/17 open in chrome
            });
        }
    }
}
