package com.kaamel.simpletwitterclient.twitteritems;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 10/2/17.
 */

public class VideoVariant implements Parcelable {

    @SerializedName("bitrate")
    public int bitrate;

    @SerializedName("content_type")
    public String contentType;

    @SerializedName("url")
    public String url;

    protected VideoVariant(Parcel in) {
        bitrate = in.readInt();
        contentType = in.readString();
        url = in.readString();
    }

    public static final Creator<VideoVariant> CREATOR = new Creator<VideoVariant>() {
        @Override
        public VideoVariant createFromParcel(Parcel in) {
            return new VideoVariant(in);
        }

        @Override
        public VideoVariant[] newArray(int size) {
            return new VideoVariant[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bitrate);
        dest.writeString(contentType);
        dest.writeString(url);
    }
}
