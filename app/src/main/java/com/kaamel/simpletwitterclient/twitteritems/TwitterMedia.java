package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by kaamel on 10/1/17.
 */

@Parcel
public class TwitterMedia {
    @SerializedName("media_url")
    public String url;

    @SerializedName("type")
    public String type;

    @SerializedName("video_info")
    public VideoInfo videoInfo;

    public TwitterMedia() {

    }
}
