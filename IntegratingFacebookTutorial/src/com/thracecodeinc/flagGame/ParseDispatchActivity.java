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
    Button multiplayerButton, singleplayerButton;

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

        multiplayerButton = (Button) findViewById(R.id.multiplayer_button);
        singleplayerButton = (Button) findViewById(R.id.singleplayer_button);

        singleplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ParseDispatchActivity.this, StartPageSinglePlayer.class);
                startActivity(i);

            }
        });

        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if there is current user info
                if (ParseUser.getCurrentUser() != null) {

                    // Start an intent for the logged in activity
                    startActivity(new Intent(ParseDispatchActivity.this, StartPageMultiplayer.class));
                } else {
                    // Start and intent for the logged out activity
                    startActivity(new Intent(ParseDispatchActivity.this, ParseSignUpOrLoginActivity.class));
                }
            }
        });

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
