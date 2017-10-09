package com.kaamel.simpletwitterclient.models;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.api.BaseApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaamel.simpletwitterclient.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/scribejava/scribejava/tree/master/scribejava-apis/src/main/java/com/github/scribejava/apis
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final BaseApi REST_API_INSTANCE = TwitterApi.instance(); //FlickrApi.instance(FlickrApi.FlickrPerm.WRITE); // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "VoUT7pV2dAhi2tLxBmMjlGgQI";       // Change this
	public static final String REST_CONSUMER_SECRET = "SCgsEp11wgAeMP74sh2VVIJOzc6k53AEJnwm8fCTVKKXLnQvO1"; // Change this

	// Landing page to indicate the OAuth flow worked in case Chrome for Android 25+ blocks navigation back to the app.
	public static final String FALLBACK_URL = "https://codepath.github.io/android-rest-client-template/success.html";

	// See https://developer.chrome.com/multidevice/android/intents
	public static final String REST_CALLBACK_URL_TEMPLATE = "intent://%s#Intent;action=android.intent.action.VIEW;scheme=%s;package=%s;S.browser_fallback_url=%s;end";

	//Twitter API end points//////////////////////
	// Home timeline
	private static final String END_POINT_HOME_TIMELINE = "statuses/home_timeline.json";
	// Home timeline
	private static final String END_POINT_MENTIONS_TIMELINE = "statuses/mentions_timeline.json";
	// User timeline
	private static final String END_POINT_USER_TIMELINE = "statuses/user_timeline.json";
	// User Lookup
	private static final String END_POINT_USERS_LOOKUP = "statuses/mentions_timeline.json";

	//account/verify_credentials.json
	private static final String END_POINT_USER_PROFILE = "account/verify_credentials.json";
	//direct_messages/events/list.json
	private static final String END_POINT_DIRECT_MESSAGES = "direct_messages/events/list.json";

	// POST statuses/update.json
	// status=Posting from %40apigee's API test console. It's like a command line for the Twitter API! %23apitools
	// display_coordinates=false HTTP/1.1
	private static final String END_POINT_POST_TWEET = "statuses/update.json";
	//favorites/create.json
	private static final String END_POINT_POST_FAVOR = "favorites/create.json";
	//favorites/destroy.json
	private static final String END_POINT_POST_UNFAVOR = "favorites/destroy.json";

	private static final Gson gson = new GsonBuilder().create();

	public TwitterClient(Context context) {
		super(context, REST_API_INSTANCE,
				REST_URL,
				REST_CONSUMER_KEY,
				REST_CONSUMER_SECRET,
				String.format(REST_CALLBACK_URL_TEMPLATE, context.getString(R.string.intent_host),
						context.getString(R.string.intent_scheme), context.getPackageName(), FALLBACK_URL));
	}

	/**
	 *
	 * @param handler will be called back
	 * @param sinceId limit the results to tweet ids more than this (newer tweets than this one)
	 * @param maxId limit the results to tweet ids less than this (tweets older than this one
	 */
	public void getHomeTimeline(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		getCallTweetRequest(handler, -1, null, sinceId, maxId, END_POINT_HOME_TIMELINE);
	}

	public void getMentionsTimeline(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		getCallTweetRequest(handler, -1, null, sinceId, maxId, END_POINT_MENTIONS_TIMELINE);
	}

	public void getDirectMessages(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		getCallTweetRequest(handler, -1, null, sinceId, maxId, END_POINT_DIRECT_MESSAGES);
	}

	public void getUserTimeline(AsyncHttpResponseHandler handler, long uid, String handle, long sinceId, long maxId) {
		getCallTweetRequest(handler, uid, handle, sinceId, maxId, END_POINT_USER_TIMELINE);
	}

	private void getCallTweetRequest(AsyncHttpResponseHandler handler, long uid, String twitterHandle, long sinceId, long maxId, String endPoint) {
		String apiUrl = getApiUrl(endPoint);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("since_id", Long.valueOf(sinceId));
		if (maxId > 1)
			params.put("max_id", Long.valueOf(maxId));

		if (uid > 0 && twitterHandle != null) {
			params.put("screen_name", twitterHandle);
			params.put("user_id", uid);
		}
		client.get(apiUrl, params, handler);
	}

	private void userLookupById(AsyncHttpResponseHandler handler, long sinceId, long maxId) {
		String apiUrl = getApiUrl(END_POINT_USERS_LOOKUP);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		params.put("since_id", Long.valueOf(sinceId));
		if (maxId > 0)
			params.put("max_id", Long.valueOf(maxId));
		client.get(apiUrl, params, handler);
	}

	public void getSelf(AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl(END_POINT_USER_PROFILE);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("count", "25");
		client.get(apiUrl, params, handler);
	}

	public void postTweet(String body, AsyncHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/update.json");
		RequestParams params = new RequestParams();
		params.put("status", body);
		getClient().post(apiUrl, params, handler);
	}

	public void postTweet(AsyncHttpResponseHandler handler, String body) {
		String apiUrl = getApiUrl(END_POINT_POST_TWEET);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("status", body);
		client.post(apiUrl, params, handler);
	}

	public void postFavor(AsyncHttpResponseHandler handler, long tweeterId) {
		String apiUrl = getApiUrl(END_POINT_POST_FAVOR);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweeterId);
		client.post(apiUrl, params, handler);
	}

	public void postUnFavor(AsyncHttpResponseHandler handler, long tweeterId) {
		String apiUrl = getApiUrl(END_POINT_POST_UNFAVOR);
		// Can specify query string params directly or through RequestParams.
		RequestParams params = new RequestParams();
		params.put("id", tweeterId);
		client.post(apiUrl, params, handler);
	}

	public void getTimeline(String which, JsonHttpResponseHandler jsonHttpResponseHandler, long sinceId, long maxId) {
		if ("home".equals(which)) {
			getHomeTimeline(jsonHttpResponseHandler, sinceId, maxId);
		}
		else if ("mentions".equals(which)) {
			getMentionsTimeline(jsonHttpResponseHandler, sinceId, maxId);
		}
		else if ("emails".equals(which)) {
			getDirectMessages(jsonHttpResponseHandler, sinceId, maxId);
		}
	}

	public void getUserTimeline(String which, JsonHttpResponseHandler jsonHttpResponseHandler, long uid, String uName, long sinceId, long maxId) {
		if ("home".equals(which)) {
			getHomeTimeline(jsonHttpResponseHandler, sinceId, maxId);
		}
		else if ("mentions".equals(which)) {
			getMentionsTimeline(jsonHttpResponseHandler, sinceId, maxId);
		}
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
