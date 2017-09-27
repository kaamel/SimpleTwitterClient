package com.kaamel.simpletwitterclient.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 9/26/17.
 */

public class User {

    @SerializedName("name")
    public String name;

    @SerializedName("id")
    public long uid;

    @SerializedName("screen_name")
    public String twitterHandle;

    @SerializedName("profile_image_url")
    public String profileImageUrl;

    public User(String name, long uid, String twitterHandle, String profileImageUrl) {
        this.name = name;
        this.uid = uid;
        this.twitterHandle = twitterHandle;
        this.profileImageUrl = profileImageUrl;
    }
}
