package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseUser;
import com.thracecodeinc.multiplayer.ChallengeParseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by katiahristova on 4/17/15.
 */
public class StartPageSinglePlayer extends FragmentActivity {
    private HashMap<String, Boolean> regionsMap;
    Button playOnlineButton;
    static Button playOfflineButton;
    Button playHangmanButton;
    Button selectRegionsButton;
    Button optionsButton;

    ProfileUpdateDialog profileUpdateDialog;

    int guessRows = 0;
    boolean dataOn;
    boolean wifiOn;
    boolean networkAllowed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_single_player_start_activity_layout);

        //Flag for LittleHands permissions
        networkAllowed = true;

        String actionBarTitle = getString(R.string.app_name);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));



        regionsMap = new HashMap<>();


        Log.d("MyApp", "Regions map in start page: " + regionsMap.toString());


        playOnlineButton = (Button) findViewById(R.id.buttonPlay);
        playOfflineButton = (Button) findViewById(R.id.buttonPlayOffline);
        playHangmanButton = (Button) findViewById(R.id.buttonPlayHangman);
        selectRegionsButton = (Button) findViewById(R.id.buttonSelectRegions);
        optionsButton = (Button) findViewById(R.id.buttonOptions);

        playOnlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if data and wifi are on
                dataOn = dataState();
                wifiOn = wifiState();
                boolean online = (dataOn || wifiOn) && networkAllowed;
                if (online) {
                    Intent i = new Intent(getApplicationContext(), OnlineGame.class);
                    showSelectNumberOfChoicesPopup(i);
                } else {
                    if (!SharedMethods.isOnline(getApplicationContext()))
                        SharedMethods.networkModePopup(StartPageSinglePlayer.this, regionsMap, guessRows);
                }
            }
        });

        playOfflineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), OfflineGame.class);
                i.putExtra("startedByUser",true);
                i.putExtra("multiplayer", false);
                showSelectNumberOfChoicesPopup(i);
            }
        });

        playHangmanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HangmanPlayActivity.class);
                startActivity(i);
            }
        });

        selectRegionsButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                regionsMap = SharedMethods.getRegionsMap(StartPageSinglePlayer.this);

            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedMethods.optionsPopup(StartPageSinglePlayer.this);
            }
        });
    }

    public void showSelectNumberOfChoicesPopup(final Intent i)
    {

        final String[] possibleChoices =
                getResources().getStringArray(R.array.guessesList);

        AlertDialog.Builder choicesBuilder =
                new AlertDialog.Builder(StartPageSinglePlayer.this);
        choicesBuilder.setTitle(R.string.choices);

        choicesBuilder.setItems(R.array.guessesList,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        guessRows = Integer.parseInt(
                                possibleChoices[item].toString()) / 2;
                        i.putExtra("guessRows", guessRows);
                        i.putExtra("regionsMap", regionsMap);
                        startActivity(i);
                        finish();
                    }
                }
        );
        AlertDialog choicesDialog = choicesBuilder.create();
        choicesDialog.show();
    }

    //Get the DATA state
    public boolean dataState() {
        boolean mobileDataEnabled = false; // Assume disabled
        if (SharedMethods.isOnline(getApplicationContext())) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            try {
                Class cmClass = Class.forName(cm.getClass().getName());
                Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
                method.setAccessible(true);
                // Make the method callable
                // get the setting for "mobile data"

                mobileDataEnabled = (Boolean) method.invoke(cm);
            } catch (Exception e) { // Some problem accessible private API // TODO do wh
            }
        }
        return mobileDataEnabled;
    }



    //Get the wifi state
    public boolean wifiState() {
        if (SharedMethods.isOnline(getApplicationContext())){
            WifiManager mng = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (mng.isWifiEnabled())
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_page_options_menu, menu);
        MenuItem loginMenuItem = menu.findItem(R.id.action_settings);

        if (ParseUser.getCurrentUser() != null) {
            loginMenuItem.setTitle(getString(R.string.logout));
        } else {
            loginMenuItem.setTitle(getString(R.string.login));
        }


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mi = (MenuItem) menu.findItem(R.id.user_pic);

        if (ParseUser.getCurrentUser() == null) {
            mi.setVisible(false);
        }
        else {
            String dir = Environment.getExternalStorageDirectory().toString();
            File imgFile = new  File(dir, ParseUser.getCurrentUser().getUsername() + "flagGameProfilePic.jpg");
            if (imgFile.exists()) {

                Drawable d1 = Drawable.createFromPath(imgFile.getAbsolutePath());
                Bitmap bitmap = ((BitmapDrawable) d1).getBitmap();
            // Scale it to 50 x 50
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 150, 150, true));
                mi.setIcon(d);
            }
            else {
                mi.setIcon(R.drawable.button_add_pic);
            }
        }

        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.getCurrentUser().logOut();
            startActivity(new Intent(StartPageSinglePlayer.this, ParseDispatchActivity.class));
            return true;
        }

        if (id == R.id.user_pic)
        {
            profileUpdateDialog = new ProfileUpdateDialog();
            FragmentManager manager = getSupportFragmentManager();
            profileUpdateDialog.show(manager, "");

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileUpdateDialog.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public void onBackPressed() {
       Intent i = new Intent(this, ParseDispatchActivity.class);
        startActivity(i);
        finish();
    }

}
