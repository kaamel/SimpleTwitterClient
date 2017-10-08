package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by kaamel on 9/26/17.
 */

@Parcel
public class TweetInternal {

    @SerializedName("text")
    public String body;

    @SerializedName("id")
    public long id;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("user")
    public User user;

    @SerializedName("extended_entities")
    public TwitterEntities entities;

    //////////////////////////////////////////////////////////////////
    //Expanded values for part 2
    //"favorite_count":1138
    //"reply_count":1585
    //"retweet_count":1585
    //////////////////////////////////////////////////////////////////

    @SerializedName("favorite_count")
    int favoritCount;

    @SerializedName("reply_count")
    int replyCount;

    @SerializedName("retweet_count")
    int retweet_count;

    @SerializedName("favorited")
    boolean favorited;

    public TweetInternal() {

    }
}
