package com.kaamel.simpletwitterclient.twitteritems;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kaamel on 10/2/17.
 */

public class VideoInfo implements Parcelable {

    @SerializedName("aspect_ratio")
    public int[] aspectRatio;

    @SerializedName("duration_millis")
    public long duration;

    @SerializedName("variants")
    public List<VideoVariant> variants;

    protected VideoInfo(Parcel in) {
        aspectRatio = in.createIntArray();
        duration = in.readLong();
        variants = in.createTypedArrayList(VideoVariant.CREATOR);
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(aspectRatio);
        dest.writeLong(duration);
        dest.writeTypedList(variants);
    }
}
