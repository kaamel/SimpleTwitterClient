package com.kaamel.simpletwitterclient.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.databinding.ItemTweetBinding;
import com.kaamel.simpletwitterclient.models.Tweet;
import com.kaamel.simpletwitterclient.utils.*;


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

        public ViewHolder(View itemView) {
            super(itemView);
            binding = ItemTweetBinding.bind(itemView);
            tvBody = binding.tvBody;
            tvUserName = binding.tvUserName;
            tvCreatedAt = binding.tvCreatedAt;
            tvTwitterHandle = binding.tvTwitterHandle;
            ivProfileImage = binding.ivProfileImage;
        }
    }
}
