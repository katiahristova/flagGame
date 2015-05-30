package com.thracecodeinc.multiplayer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.thracecodeinc.challengeBO.ChallengeBO;
import com.thracecodeinc.flagGame.MainActivity;
import com.thracecodeinc.flagGame.OfflineGame;
import com.thracecodeinc.flagGame.OnlineGame;
import com.thracecodeinc.flagGame.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String uniqueid;
    @Override
    public void onReceive(final Context context, Intent intent) {

//Get JSON data and put them into variables
        try {

            JSONObject json = new JSONObject(intent.getExtras().getString(
                    "com.parse.Data"));

            alert = json.getString("alert").toString();
            fromUser = json.getString("fromuser").toString();
            uniqueid = json.getString("uniquechallengeid".toString());

        } catch (JSONException e) {

        }

//You can specify sound
        notifySound = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.flags); //You can change your icon
        mBuilder.setContentText(fromUser + "\n" + alert);
        mBuilder.setContentTitle("You are challenged");
        mBuilder.setSound(notifySound);
        mBuilder.setAutoCancel(true);

// this is the activity that we will send the user, change this to anything you want

        regionsMap = new HashMap<>();
        String[] regionNms =
                context.getResources().getStringArray(R.array.regionsList);
        for (String region : regionNms)
            regionsMap.put(region, false);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        query.whereEqualTo("uniqueID", uniqueid);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        ArrayList<String> regionsFromParse = (ArrayList<String>) list.get(0).get("regions");

                        for (String region : regionsFromParse) {
                                regionsMap.put(region, true);
                        }


                        resultIntent = new Intent(context, OfflineGame.class);
                        resultIntent.putExtra("guessRows", list.get(0).getInt("Choices"));
                        resultIntent.putExtra("regionsMap", regionsMap);
                        resultIntent.putExtra("startedByUser", true);
                        resultIntent.putExtra("multiplayer", false);
                        resultIntent.putExtra("fromChallenge", true);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                                0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager notificationManager = (NotificationManager) context
                                .getSystemService(context.NOTIFICATION_SERVICE);

                        notificationManager.notify(mNotificationId, mBuilder.build());

                    }
                }

            }
        });




    }

}
