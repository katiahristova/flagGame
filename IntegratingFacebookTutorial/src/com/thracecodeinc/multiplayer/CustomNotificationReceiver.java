package com.thracecodeinc.multiplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thracecodeinc.flagGame.OfflineGame;
import com.thracecodeinc.flagGame.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Samurai on 5/29/15.
 */
public class CustomNotificationReceiver extends BroadcastReceiver {
    private HashMap<String, Boolean> regionsMap;
    NotificationCompat.Builder mBuilder;
    Intent resultIntent;
    int mNotificationId = 001;
    Uri notifySound;

    String alert; // This is the message string that send from push console
    String fromUser;
    @Override
    public void onReceive(final Context context, Intent intent) {

//Get JSON data and put them into variables
        try {

            JSONObject json = new JSONObject(intent.getExtras().getString(
                    "com.parse.Data"));

            alert = json.getString("alert").toString();
            fromUser = json.getString("fromuser").toString();

        } catch (JSONException e) {

        }

//You can specify sound
        notifySound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.globe); //You can change your icon

        if (alert != null) {

            if (alert.contains("Countries Challenge from")) {
                mBuilder.setContentTitle("You are challenged");
                mBuilder.setContentText(alert + "\n" + fromUser);
            } else {
                mBuilder.setContentTitle("Challenge completed");
                mBuilder.setContentText(fromUser + "\n" + alert);
            }
        }

        mBuilder.setSound(notifySound);
        mBuilder.setAutoCancel(true);

// this is the activity that we will send the user, change this to anything you want

        resultIntent = new Intent(context, ChallengePreview.class);
        resultIntent.putExtra("startedByUser", true);
        resultIntent.putExtra("multiplayer", false);
        resultIntent.putExtra("fromChallenge", true);
        resultIntent.putExtra("fromuser", fromUser);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(mNotificationId, mBuilder.build());






    }

}
