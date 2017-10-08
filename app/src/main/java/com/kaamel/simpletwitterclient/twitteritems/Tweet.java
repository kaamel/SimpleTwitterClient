package com.kaamel.simpletwitterclient.twitteritems;

import com.kaamel.simpletwitterclient.models.TwitterDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by kaamel on 9/26/17.
 */

@Table(database = TwitterDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {

    @Column
    String body;

    @PrimaryKey
    @Column
    long id;

    @Column
    String createdAt;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;

    @Column
    String mediaUrl;

    @Column
    String mediaType;

    @Column
    int videoWidth;

    @Column
    int videoHeight;

    @Column
    long videoDuration;

    @Column
    int videoBitrate;

    @Column
    String videoContentType;

    @Column
    String videoUrl;

    //////////////////////////////////////////////////////////////////
    //Expanded values for part 2
    //"favorite_count":1138
    //"reply_count":1585
    //"retweet_count":1585
    //////////////////////////////////////////////////////////////////

    @Column
    int favoritCount;

    @Column
    int replyCount;

    @Column
    int retweetCount;

    @Column
    boolean favorited;

    public Tweet() {

    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public long getVideoDuration() {
        return videoDuration;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public String getVideoContentType() {
        return videoContentType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public int getFavoritCount() {
        return favoritCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public static List<Tweet> recentTweets(int howMany) {
        return new Select().from(Tweet.class).orderBy(Tweet_Table.createdAt, false).limit(howMany).queryList();
    }

    public static void replaceAllTweets(List<Tweet> tweets) {
        Delete.table(Tweet.class);
        for (Tweet tweet: tweets) {
            tweet.save();
        }
    }

    public static void addUpdateTweets(List<Tweet> tweets) {
        for (Tweet tweet : tweets) {
            //TweetNew tweet = new Select().from(TweetNew.class).where(TweetNew_Table.id.is(tweetNew.id)).querySingle();
            tweet.save();
        }
    }

    public static void removeallTweets() {
        Delete.table(Tweet.class);
    }
}
