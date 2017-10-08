package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by kaamel on 10/4/17.
 */

public class TweetJsonSerializer implements JsonSerializer<Tweet> {
    @Override
    public JsonElement serialize(Tweet src, Type typeOfSrc, JsonSerializationContext context) {

        VideoVariant variant = new VideoVariant();
        variant.url = src.videoUrl;
        variant.contentType = src.videoContentType;
        variant.bitrate = src.videoBitrate;

        VideoInfo info = new VideoInfo();
        info.variants.add(variant);
        info.aspectRatio = new int[2];
        info.aspectRatio[0] = src.videoWidth;
        info.aspectRatio[1] = src.videoHeight;
        info.variants = new ArrayList<>();
        info.duration = src.videoDuration;

        TwitterMedia media = new TwitterMedia();
        media.videoInfo = info;
        media.type = src.mediaType;
        media.url = src.mediaUrl;

        TwitterEntities entities = new TwitterEntities();
        entities.medias = new ArrayList<>();
        entities.medias.add(media);

        TweetInternal tweetInternal = new TweetInternal();
        tweetInternal.user = src.user;
        tweetInternal.body = src.body;
        tweetInternal.createdAt = src.createdAt;
        tweetInternal.id = src.id;
        tweetInternal.replyCount = src.replyCount;
        tweetInternal.retweet_count = src.retweetCount;
        tweetInternal.favoritCount = src.favoritCount;
        tweetInternal.favorited = src.favorited;
        tweetInternal.entities = entities;
        return  context.serialize(tweetInternal);
    }
}
