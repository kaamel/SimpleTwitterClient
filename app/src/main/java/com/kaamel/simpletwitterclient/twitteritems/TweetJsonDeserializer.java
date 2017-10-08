package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by kaamel on 10/4/17.
 */

public class TweetJsonDeserializer implements JsonDeserializer<Tweet> {
    @Override
    public Tweet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        TweetInternal tweetInternal = context.deserialize(json, TweetInternal.class);
        if (tweetInternal == null)
            return null;
        Tweet tweet = new Tweet();
        tweet.user = tweetInternal.user;
        tweet.id = tweetInternal.id;
        tweet.body = tweetInternal.body;
        tweet.createdAt = tweetInternal.createdAt;
        tweet.replyCount = tweetInternal.replyCount;
        tweet.favoritCount = tweetInternal.favoritCount;
        tweet.retweetCount = tweetInternal.retweet_count;
        tweet.favorited = tweetInternal.favorited;

        if (tweetInternal.entities != null && tweetInternal.entities.medias != null && tweetInternal.entities.medias.size() >0 ) {
            TwitterMedia media = tweetInternal.entities.medias.get(0);
            tweet.mediaUrl = media.url;
            tweet.mediaType = media.type;
            VideoInfo info = media.videoInfo;
            if (info != null) {
                tweet.videoWidth = info.aspectRatio[0];
                tweet.videoHeight = info.aspectRatio[1];
                tweet.videoDuration = info.duration;
                VideoVariant variant = info.variants.get(0);
                tweet.videoBitrate = variant.bitrate;
                tweet.videoContentType = variant.contentType;
                tweet.videoUrl = variant.url;
            }
        }
        return tweet;
    }
}
