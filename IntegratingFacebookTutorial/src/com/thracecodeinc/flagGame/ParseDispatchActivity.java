package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.applinks.AppLinkData;
import com.parse.Parse;
import com.parse.ParseUser;

import bolts.AppLinks;

/**
 * Created by Katia on 04/26/2015.
 */

public class ParseDispatchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));

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
        // Check if there is current user info
        if (ParseUser.getCurrentUser() != null) {
            // Start an intent for the logged in activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, ParseSignUpOrLoginActivity.class));
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
}
