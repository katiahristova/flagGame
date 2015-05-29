package com.thracecodeinc.flagGame;

import android.app.Application;
import android.app.Notification;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.thracecodeinc.flagGame.R;

public class flagGameApplication extends Application {

  static final String TAG = "MyApp";

  @Override
  public void onCreate() {
    super.onCreate();
      Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_id));
      ParseFacebookUtils.initialize(getApplicationContext());

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

      PushService.setDefaultPushCallback(this, OfflineGame.class);
  }



}
