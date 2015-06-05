package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.thracecodeinc.multiplayer.ChallengeParseUser;
import com.thracecodeinc.multiplayer.ChallengePreview;
import com.thracecodeinc.multiplayer.GameCompletedPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by katiahristova on 4/17/15.
 */
public class StartPageMultiplayer extends FragmentActivity {
    private HashMap<String, Boolean> regionsMap;
    Button playHangmanButton;
    Button selectRegionsButton;
    Button multiplayer;
    Button seeChallenge;
    Button optionsButton;
    private Button completedChallenges;
    int guessRows = 0;
    boolean networkAllowed;

    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    String picturePath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_multiplayer_start_activity_layout);

        //Flag for LittleHands permissions
        networkAllowed = true;

        String actionBarTitle = getString(R.string.app_name);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));



        regionsMap = new HashMap<>();

        playHangmanButton = (Button) findViewById(R.id.buttonPlayHangman);
        selectRegionsButton = (Button) findViewById(R.id.buttonSelectRegions);
        multiplayer = (Button) findViewById(R.id.buttonMultiplayer);
        seeChallenge = (Button) findViewById(R.id.see_challenges);
        optionsButton = (Button) findViewById(R.id.buttonOptions);
        completedChallenges = (Button) findViewById(R.id.see_challenge_completed);

        completedChallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), GameCompletedPreview.class);
                startActivity(i);
            }
        });

        seeChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ChallengePreview.class);
                startActivity(i);
            }
        });

        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChallengeParseUser challengeParseUser = new ChallengeParseUser();
                android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
                challengeParseUser.show(manager, "");
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
                regionsMap = SharedMethods.getRegionsMap(StartPageMultiplayer.this);

            }
        });

        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedMethods.optionsPopup(StartPageMultiplayer.this);
            }
        });
    }

//    public void showSelectNumberOfChoicesPopup(final Intent i)
//    {
//
//        final String[] possibleChoices =
//                getResources().getStringArray(R.array.guessesList);
//
//        AlertDialog.Builder choicesBuilder =
//                new AlertDialog.Builder(StartPageMultiplayer.this);
//        choicesBuilder.setTitle(R.string.choices);
//
//        choicesBuilder.setItems(R.array.guessesList,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//                        guessRows = Integer.parseInt(
//                                possibleChoices[item].toString()) / 2;
//                        i.putExtra("guessRows", guessRows);
//                        i.putExtra("regionsMap", regionsMap);
//                        startActivity(i);
//                        finish();
//                    }
//                }
//        );
//        AlertDialog choicesDialog = choicesBuilder.create();
//        choicesDialog.show();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start_page_multiplayer_menu, menu);

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

        if (id == R.id.user_pic)
        {
            Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
            startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedMethods.updatePhoto(this, picturePath, requestCode, resultCode, GALLERY_ACTIVITY_CODE, RESULT_CROP, data);
    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, ParseDispatchActivity.class);
        startActivity(i);
        finish();
    }
}
