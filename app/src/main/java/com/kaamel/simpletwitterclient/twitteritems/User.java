package com.kaamel.simpletwitterclient.twitteritems;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 9/26/17.
 */

public class User implements Parcelable{

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

    protected User(Parcel in) {
        name = in.readString();
        uid = in.readLong();
        twitterHandle = in.readString();
        profileImageUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(uid);
        dest.writeString(twitterHandle);
        dest.writeString(profileImageUrl);
    }
}
