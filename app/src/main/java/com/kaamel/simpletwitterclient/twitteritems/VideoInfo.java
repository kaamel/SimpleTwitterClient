package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by kaamel on 10/2/17.
 */

@Parcel
public class VideoInfo {

    @SerializedName("aspect_ratio")
    public int[] aspectRatio;

    @SerializedName("duration_millis")
    public long duration;

    @SerializedName("variants")
    public List<VideoVariant> variants;

    public VideoInfo() {

    }
}
