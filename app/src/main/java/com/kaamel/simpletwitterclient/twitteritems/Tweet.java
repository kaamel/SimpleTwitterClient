package com.kaamel.simpletwitterclient.twitteritems;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaamel on 9/26/17.
 */

public class Tweet implements Parcelable {

    @SerializedName("text")
    public String body;

    @SerializedName("id")
    public long id;

    @SerializedName("created_at")
    public String createdAt;

    @SerializedName("user")
    public User user;

    @SerializedName("entities")
    public TwitterEntities entities;

    protected Tweet(Parcel in) {
        body = in.readString();
        id = in.readLong();
        createdAt = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        entities = in.readParcelable(TwitterEntities.class.getClassLoader());
    }

    public Tweet() {

    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(body);
        dest.writeLong(id);
        dest.writeString(createdAt);
        dest.writeParcelable(user, 0);
        dest.writeParcelable(entities, 0);
    }
}
