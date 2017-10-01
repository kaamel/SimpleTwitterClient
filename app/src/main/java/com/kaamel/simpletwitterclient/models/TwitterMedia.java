package com.kaamel.simpletwitterclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 10/1/17.
 */

public class TwitterMedia implements Parcelable {
    @SerializedName("media_url")
    public String url;

    @SerializedName("type")
    public String type;

    protected TwitterMedia(Parcel in) {
        url = in.readString();
        type = in.readString();
    }

    public static final Creator<TwitterMedia> CREATOR = new Creator<TwitterMedia>() {
        @Override
        public TwitterMedia createFromParcel(Parcel in) {
            return new TwitterMedia(in);
        }

        @Override
        public TwitterMedia[] newArray(int size) {
            return new TwitterMedia[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(type);
    }
}
