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
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.callbacks.OnFragmentInteractionListener;
import com.kaamel.simpletwitterclient.databinding.ItemTweetBinding;
import com.kaamel.simpletwitterclient.twitteritems.Tweet;
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
        holder.tvBody.setText(tweet.getBody());
        holder.tvBody.setTag("notclicked");
        new PatternEditableBuilder().
                addPattern(Pattern.compile("#(\\w+)"), Color.BLUE,
                        text -> {
                            ((OnFragmentInteractionListener) context).onHashtagClicked(text);
                            holder.tvBody.setTag("clicked");
                }).into(holder.tvBody);

        new PatternEditableBuilder().
                addPattern(Pattern.compile("\\@(\\w+)"), Color.BLUE,
                        text -> {
                            ((OnFragmentInteractionListener) context).onUserHandleClicked(text);
                            holder.tvBody.setTag("clicked");
                        }).into(holder.tvBody);

        String name = tweet.getUser().getName() == null?"": tweet.getUser().getName();
        holder.tvUserName.setText(name);
        String twitterHnadle = tweet.getUser().getTwitterHandle() == null?"":("@" + tweet.getUser().getTwitterHandle());
        holder.tvTwitterHandle.setText(twitterHnadle);
        holder.tvCreatedAt.setText(Utils.twitterTimeToDiffFromNow(tweet.getCreatedAt()));
        Glide.with(context)
                .load(tweet.getUser().getProfileImageUrl())
                .placeholder(R.drawable.ic_action_twitter)
                .error(android.R.drawable.stat_notify_error)
                .bitmapTransform(new RoundedCornersTransformation( context, 37, 2))
                .into(holder.ivProfileImage);

        if (tweet.getMediaUrl() != null && tweet.getMediaType() != null) {
            if (tweet.getMediaType().equals("photo")) {
                Glide.with(context)
                        .load(tweet.getMediaUrl())
                        .placeholder(R.drawable.ic_action_twitter)
                        .error(android.R.drawable.stat_notify_error)
                        .bitmapTransform(new RoundedCornersTransformation(context, 37, 2))
                        .into(holder.ivEmbedded);
                holder.vvEmbedded.setVisibility(View.GONE);
                holder.ivEmbedded.setVisibility(View.VISIBLE);
            }
            else if (tweet.getMediaType().equals("animated_gif") || tweet.getMediaType().equals("video")){
                mediaController.setAnchorView(holder.vvEmbedded);
                holder.vvEmbedded.setMediaController(mediaController);
//                if (tweet.mediaType.equals("video"))
                    holder.vvEmbedded.setVideoURI(Uri.parse(tweet.getVideoUrl()));
//                else
//                    holder.vvEmbedded.setVideoURI(Uri.parse(tweet.mediaUrl));
                holder.vvEmbedded.requestFocus();
                holder.vvEmbedded.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                //holder.vvEmbedded.start();
                holder.vvEmbedded.setVisibility(View.VISIBLE);
                holder.ivEmbedded.setVisibility(View.GONE);
            }

        }
        else {
            holder.vvEmbedded.setVisibility(View.GONE);
            holder.ivEmbedded.setVisibility(View.GONE);
        }

        holder.tvReplyCount.setText("");
        holder.tvRetweetCount.setText("");
        holder.tvLikeCount.setText("");

        if (tweet.getReplyCount() >0 )
            holder.tvReplyCount.setText(String.valueOf(tweet.getReplyCount()));
        if (tweet.getRetweetCount() > 0)
            holder.tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        if (tweet.getFavoritCount() > 0)
            holder.tvLikeCount.setText(String.valueOf(tweet.getFavoritCount()));

        if (tweet.isFavorited())
            holder.ibLike.setImageResource(R.drawable.ic_action_twitter_like_red);
        else
            holder.ibLike.setImageResource(R.drawable.ic_action_twitter_like);

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

        TextView tvReplyCount;
        TextView tvRetweetCount;
        TextView tvLikeCount;

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
            ibEmail = binding.layout.ibEmail;
            ibLike = binding.layout.ibLike;
            ibReply = binding.layout.ibReply;
            ibRetweet = binding.layout.ibRetweet;

            tvReplyCount = binding.layout.tvReplyCount;
            tvRetweetCount = binding.layout.tvRetwetCount;
            tvLikeCount = binding.layout.tvLikeCount;

            ibEmail.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onEmailClicked(position);
            });

            ibReply.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onReplyClicked(position);
            });

            ibRetweet.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onRetweetClicked(position);
            });

            ibLike.setOnClickListener(v -> {
                final int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onLikeClicked(position);
            });

            View.OnClickListener detailViewListener = v -> {
                final int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onTweetClicked(position);
            };

            View.OnClickListener userNameListener = v -> {
                String name = ((TextView) v).getText().toString();
                ((OnFragmentInteractionListener) itemView.getContext()).onUserNameClicked(getAdapterPosition());
            };

            View.OnClickListener userHandleListener = v -> {
                String handle = ((TextView) v).getText().toString();
                ((OnFragmentInteractionListener) itemView.getContext()).onUserHandleClicked(getAdapterPosition());
            };

            View.OnClickListener userProfileListener = v -> {
                int position = getAdapterPosition();
                ((OnFragmentInteractionListener) itemView.getContext()).onUserProfileClicked(position);
            };

            tvCreatedAt.setOnClickListener(detailViewListener);
            tvUserName.setOnClickListener(userNameListener);
            tvTwitterHandle.setOnClickListener(userHandleListener);
            ivProfileImage.setOnClickListener(userProfileListener);
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
                ((OnFragmentInteractionListener) itemView.getContext()).onTweetClicked(position);
            });
        }
    }

    public interface OnItemClickListener {
        public void onClick(View view, int position);
    }
}
