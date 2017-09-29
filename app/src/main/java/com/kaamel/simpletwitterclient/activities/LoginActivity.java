package com.kaamel.simpletwitterclient.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.kaamel.simpletwitterclient.R;
import com.kaamel.simpletwitterclient.models.TwitterClient;
import com.kaamel.simpletwitterclient.databinding.ActivityLoginBinding;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient> {

	ActivityLoginBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
		//Toast.makeText(this, "Logged in succerssfully", Toast.LENGTH_LONG).show();
		Intent intent = new Intent(this, TimelineActivity.class);
        Intent i = getIntent();
        if (i!=null) {
            intent.putExtra("action", i.getAction());
			intent.putExtra("title", i.getStringExtra(Intent.EXTRA_SUBJECT));
			intent.putExtra("type", i.getType());
			intent.putExtra("body", i.getStringExtra(Intent.EXTRA_TEXT));
        }
		startActivity(intent);
		finish();
	}

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}

}
