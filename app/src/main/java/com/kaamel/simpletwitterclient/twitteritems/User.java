package com.kaamel.simpletwitterclient.twitteritems;

import com.google.gson.annotations.SerializedName;
import com.kaamel.simpletwitterclient.models.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

/**
 * Created by kaamel on 9/26/17.
 */

@Table(database = TwitterDatabase.class)
@Parcel(analyze={User.class})
public class User extends BaseModel{

    @Column
    @SerializedName("name")
    String name;

    @Column
    @PrimaryKey
    @SerializedName("id")
    long uid;

    @Column
    @SerializedName("screen_name")
    String twitterHandle;

    @Column
    @SerializedName("profile_image_url")
    String profileImageUrl;

    @Column
    @SerializedName("description")
    String description;

    @Column
    @SerializedName("followers_count")
    int followersCount;

    @Column
    @SerializedName("friends_count")
    int friends;

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getTwitterHandle() {
        return twitterHandle;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getFriendsCount() {
        return friends;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getDescription() {
        return description;
    }

    public User(String name, long uid, String twitterHandle, String profileImageUrl) {
        this.name = name;
        this.uid = uid;
        this.twitterHandle = twitterHandle;
        this.profileImageUrl = profileImageUrl;
    }

    public User() {

    }
}
