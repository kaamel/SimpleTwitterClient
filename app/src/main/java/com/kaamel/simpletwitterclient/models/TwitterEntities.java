package com.kaamel.simpletwitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kaamel on 10/1/17.
 */

public class TwitterEntities implements Parcelable {

    @SerializedName("media")
    public List<TwitterMedia> medias;

    protected TwitterEntities(Parcel in) {
    }

    public static final Creator<TwitterEntities> CREATOR = new Creator<TwitterEntities>() {
        @Override
        public TwitterEntities createFromParcel(Parcel in) {
            return new TwitterEntities(in);
        }

        @Override
        public TwitterEntities[] newArray(int size) {
            return new TwitterEntities[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
