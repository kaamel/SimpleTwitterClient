package com.kaamel.simpletwitterclient.models;

import com.kaamel.simpletwitterclient.twitteritems.Tweet;
import com.kaamel.simpletwitterclient.twitteritems.TwitterEntities;
import com.kaamel.simpletwitterclient.twitteritems.TwitterMedia;
import com.kaamel.simpletwitterclient.twitteritems.User;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

/*
 * This is a temporary, sample model that demonstrates the basic structure
 * of a SQLite persisted Model object. Check out the DBFlow wiki for more details:
 * https://github.com/codepath/android_guides/wiki/DBFlow-Guide
 *
 * Note: All models **must extend from** `BaseModel` as shown below.
 * 
 */
@Table(database = TwitterDatabase.class)
public class TweetModel extends BaseModel {

	@PrimaryKey
	@Column
	Long id;

	@Column
	private String body;

	@Column
	public String createdAt;

	@Column
	long userId;

	@Column
	String userName;

	@Column
	String userProfileImage;

	@Column
	String userTwitterHandle;

	@Column
	String mediaUrl;

	@Column
	String mediaType;

	public static List<Tweet> tweets;

	public TweetModel() {
		super();
	}

	public TweetModel(Tweet tweet){
		super();
		id = tweet.id;
		body = tweet.body;
		createdAt = tweet.createdAt;
		userId = tweet.user.uid;
		userTwitterHandle = tweet.user.twitterHandle;
		userName = tweet.user.name;
		userProfileImage = tweet.user.profileImageUrl;
		if (tweet.entities != null && tweet.entities.medias != null && tweet.entities.medias.get(0) != null) {
			mediaType = tweet.entities.medias.get(0).type;
			mediaUrl = tweet.entities.medias.get(0).url;
		}
	}

    public static List<Tweet> getTweets() {
		return tweets;
	}

	public Tweet toTweet() {
		Tweet tweet = new Tweet();
		tweet.body = body;
		tweet.createdAt = createdAt;
		TwitterEntities tn = new TwitterEntities();
		TwitterMedia tm = new TwitterMedia();
		tm.type = mediaType;
		tm.url = mediaUrl;
		List<TwitterMedia> twtm = new ArrayList<>();
		twtm.add(tm);
		tn.medias = twtm;
		tweet.entities = tn;
		tweet.user = new User(userName, userId, userTwitterHandle, userProfileImage);
		return tweet;
	}

	// Getters
	public long getId() {
		return id;
	}
	public String getBody() {
		return body;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserProfileImage() {
		return userProfileImage;
	}

	public void setUserProfileImage(String userProfileImage) {
		this.userProfileImage = userProfileImage;
	}

	public String getUserTwitterHandle() {
		return userTwitterHandle;
	}

	public void setUserTwitterHandle(String userTwitterHandle) {
		this.userTwitterHandle = userTwitterHandle;
	}

	public String getMediaUrl() {
		return mediaUrl;
	}

	public void setMediaUrl(String mediaUrl) {
		this.mediaUrl = mediaUrl;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public static List<Tweet> recentItems() {
		List<TweetModel> tms = new Select().from(TweetModel.class).orderBy(TweetModel_Table.createdAt, false).limit(300).queryList();
		List<Tweet> tweets = new ArrayList<>();
		for (TweetModel tm: tms) {
			tweets.add(tm.toTweet());
		}
		return tweets;
	}

	public static void saveTweets() {
		for (Tweet tweet: tweets) {
			new TweetModel(tweet).save();
		}
	}

	public static void loadTweets() {
		tweets = new ArrayList<>();
		List<TweetModel> tms = new Select().from(TweetModel.class).orderBy(TweetModel_Table.createdAt, false).limit(300).queryList();
		for (TweetModel tm: tms) {
			tweets.add(tm.toTweet());
		}
	}

	public static void removeall() {
		List<TweetModel> tms = new Select().from(TweetModel.class).orderBy(TweetModel_Table.createdAt, false).limit(300).queryList();
		for (TweetModel tm: tms) {
			tm.delete();
		}
	}

	public static void saveAll(List<Tweet> ts) {
		for (Tweet t: ts) {
			new TweetModel(t).save();
		}
	}
}
