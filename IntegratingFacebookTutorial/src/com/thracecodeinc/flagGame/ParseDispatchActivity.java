package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import bolts.AppLinks;

/**
 * Created by Katia on 04/26/2015.
 */

public class ParseDispatchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);
        Uri targetUrl =
                AppLinks.getTargetUrlFromInboundIntent(this, getIntent());

        if (targetUrl != null) {
            Log.d("MyApp", "App Link Target URL: " + targetUrl.toString());
            Bundle b = AppLinks.getAppLinkData(getIntent());
            Bundle x = AppLinks.getAppLinkExtras(getIntent());

            AppLinkData appLinkData = AppLinkData.createFromActivity(this);
            Bundle s = appLinkData.getArgumentBundle();

            Log.d("MyApp", "Data " + b.toString());
            Log.d("MyApp", "Extras " + x.toString());
            Log.d("MyApp", "Argument bundle " + s.toString());

            Log.d("MyApp", targetUrl.getQueryParameter("request_ids").toString());

        }
        else {
            AppLinkData.fetchDeferredAppLinkData(
                    this,
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //process applink data
                            //Bundle b = appLinkData.getArgumentBundle();
                            //Log.d("MyApp", "Request data: " + b.getString("data"));
                        }
                    });
            Log.d("MyApp", "No app link");
        }

        setContentView(R.layout.single_or_multiplayer_activity);
                // Check if there is current user info
                if (ParseUser.getCurrentUser() != null) {
                    // Start an intent for the logged in activity
                    signUserToChannel();
                    startActivity(new Intent(ParseDispatchActivity.this, StartPageMultiOrSingleplayer.class));
                } else {
                    // Start and intent for the logged out activity
                    startActivity(new Intent(ParseDispatchActivity.this, ParseSignUpOrLoginActivity.class));
                }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        //AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        //AppEventsLogger.deactivateApp(this);
    }


    public void signUserToChannel(){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("device_id", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();

        ParsePush.subscribeInBackground("ChallengeChanel", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        //PushService.setDefaultPushCallback(this, OfflineGame.class);
    }
}
