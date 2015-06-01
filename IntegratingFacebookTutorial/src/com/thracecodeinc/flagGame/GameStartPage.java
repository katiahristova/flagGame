package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thracecodeinc.challengeBO.ChallengeBO;
import com.thracecodeinc.multiplayer.ChallengeParseUser;
import com.thracecodeinc.multiplayer.ChallengePreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by katiahristova on 4/17/15.
 */
public class GameStartPage extends FragmentActivity {
    private HashMap<String, Boolean> regionsMap;
    Button playOnlineButton;
    static Button playOfflineButton;
    Button playHangmanButton;
    Button selectRegionsButton;
    Button multiplayer;
    Button seeChallenge;
    int guessRows = 0;
    int counter = 0;
    boolean dataOn;
    boolean wifiOn;
    boolean networkAllowed;

    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    String picturePath = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_start_activity_layout);

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
        multiplayer = (Button) findViewById(R.id.buttonMultiplayer);
        seeChallenge = (Button) findViewById(R.id.see_challenges);

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
                        SharedMethods.networkModePopup(GameStartPage.this, regionsMap, guessRows);
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
                regionsMap = SharedMethods.getRegionsMap(GameStartPage.this);

            }
        });
    }

    public void showSelectNumberOfChoicesPopup(final Intent i)
    {

        final String[] possibleChoices =
                getResources().getStringArray(R.array.guessesList);

        AlertDialog.Builder choicesBuilder =
                new AlertDialog.Builder(GameStartPage.this);
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
            startActivity(new Intent(GameStartPage.this, ParseDispatchActivity.class));
            return true;
        }

        if (id == R.id.user_pic)
        {
            Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
            startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);

            //Intent galleryIntent = new Intent(Intent.ACTION_PICK,
             //       android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            
// Start the Intent
            //startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
        return super.onOptionsItemSelected(item);
    }
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);

                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, "flagGameProfilePic.jpg"); // the File to save to
                fOut = new FileOutputStream(file);

                Bitmap pictureBitmap = BitmapFactory
                        .decodeFile(imgDecodableString); // obtaining the Bitmap
                pictureBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush();
                fOut.close(); // do not forget to close the stream

                MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

                cursor.close();
                invalidateOptionsMenu();


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    } */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if(resultCode == Activity.RESULT_OK){
                picturePath = data.getStringExtra("picturePath");
                Log.d("MyApp", "Filepath: " + picturePath);
                //perform Crop on the Image Selected from Gallery
                performCrop(picturePath);
            }
        }

        if (requestCode == RESULT_CROP ) {
            if(resultCode == Activity.RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                String path = Environment.getExternalStorageDirectory().toString();
                OutputStream fOut = null;
                File file = new File(path, ParseUser.getCurrentUser().getUsername() + "flagGameProfilePic.jpg"); // the File to save to
                try {
                    fOut = new FileOutputStream(file);
                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush();
                    fOut.close(); // don't forget to close the stream

                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                }
                catch (Exception e)
                {
                    Log.d("MyApp", "Exception: " + e.toString());
                }
                invalidateOptionsMenu();
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 286);
            cropIntent.putExtra("outputY", 286);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - handle returning in onActivityResult
            startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
