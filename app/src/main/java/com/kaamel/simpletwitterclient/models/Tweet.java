package com.kaamel.simpletwitterclient.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 9/26/17.
 */

public class Tweet {

    @SerializedName("text")
    public String body;

    @SerializedName("id")
    public long uid;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("user")
    public User user;
}
