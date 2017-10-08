package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by kaamel on 10/1/17.
 */

@Parcel
public class TwitterEntities {

    @SerializedName("media")
    public List<TwitterMedia> medias;

    public TwitterEntities() {
    }
}
