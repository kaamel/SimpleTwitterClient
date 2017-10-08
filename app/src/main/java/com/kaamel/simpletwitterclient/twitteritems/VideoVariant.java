package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by kaamel on 10/2/17.
 */

@Parcel
public class VideoVariant {

    @SerializedName("bitrate")
    public int bitrate;

    @SerializedName("content_type")
    public String contentType;

    @SerializedName("url")
    public String url;

    public VideoVariant() {

    }
}
