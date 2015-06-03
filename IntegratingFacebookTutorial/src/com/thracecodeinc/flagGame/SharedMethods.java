package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * Created by Samurai on 4/19/15.
 */
public class SharedMethods {
    private static int counter;
    private static HashMap<String, Boolean> regionsMap;
    static double highscore = 0.0;
    static AlertDialog resetDialog;
    private static int exitGame;
    private static Intent i = null;
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static HashMap<String, Boolean> getRegionsMap() {
        return regionsMap;
    }

    public static void setRegionsMap(HashMap<String, Boolean> regionsMap) {
        SharedMethods.regionsMap = regionsMap;
    }

    //Used when the home button is pressed
    public static void quitGamePopup(final Context context){

        String title = context.getString(R.string.quit_game);
        String message = context.getString(R.string.quit_the_game);


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setCancelable(true);
        builder.setPositiveButton(context.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                         Intent i = new Intent(context, ParseDispatchActivity.class);
                         context.startActivity(i);
                    }
                }
        );
        builder.setNegativeButton(context.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetDialog.dismiss();
                    }
                }
        );

        resetDialog = builder.create();
        resetDialog.show();

    }

    public static void updateProfilePopup(final Context c, final Activity a, final int GALLERY_ACTIVITY_CODE)
    {
        LayoutInflater li = LayoutInflater.from(c);
        //LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = li.inflate(R.layout.profile_update_popup, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setView(dialoglayout);
        ImageButton pic = (ImageButton) dialoglayout.findViewById(R.id.set_picture_button);
        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_Intent = new Intent(c.getApplicationContext(), GalleryUtil.class);
                a.startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
            }
        });
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int i) {
                di.dismiss();
            }
        });
        builder.show();
    }

    public static void optionsPopup(final Context c)
    {
        LayoutInflater li = LayoutInflater.from(c);
        //LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = li.inflate(R.layout.options_layout, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(c.getString(R.string.options));
        builder.setView(dialoglayout);
        final RadioButton country = (RadioButton) dialoglayout.findViewById(R.id.radioCountryName);
        final RadioButton capital = (RadioButton) dialoglayout.findViewById(R.id.radioCapitalCity);
        final RadioGroup numberOfQuestionsGroup = (RadioGroup) dialoglayout.findViewById(R.id.numberOfQuestions);
        SharedPreferences prefs = c.getSharedPreferences(
                "com.thracecodeinc.falgGame", Context.MODE_PRIVATE);
        boolean countryModeOld= prefs.getBoolean("countryMode", true);
        int numberOfQuestionsOld = prefs.getInt("numberOfQuestions", 10);

        if (!countryModeOld) {
            country.setChecked(false);
            capital.setChecked(true);
        }

        switch (numberOfQuestionsOld)
        {
            case 5:
                numberOfQuestionsGroup.check(R.id.number5);
                break;
            case 10:
                numberOfQuestionsGroup.check(R.id.number10);
                break;
            case 15:
                numberOfQuestionsGroup.check(R.id.number15);
                break;
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                boolean countryMode = true;
                int numberOfQuestions = 10;
                if (!country.isChecked())
                    countryMode = false;
                int id = numberOfQuestionsGroup.getCheckedRadioButtonId();
                switch (id) {
                    case R.id.number5:
                        numberOfQuestions = 5;
                        break;
                    case R.id.number10:
                        numberOfQuestions = 10;
                        break;
                    case R.id.number15:
                        numberOfQuestions = 15;
                        break;
                }
                SharedPreferences prefs = c.getSharedPreferences(
                        "com.thracecodeinc.falgGame", Context.MODE_PRIVATE);
                prefs.edit().putInt("numberOfQuestions", numberOfQuestions).apply();
                prefs.edit().putBoolean("countryMode", countryMode).apply();


                di.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface di, int i) {
                di.dismiss();
            }
        });
        builder.show();
    }

    public static void networkModePopup(final Context context, HashMap<String,Boolean> regionsMap, int guessRows){

        String title = "";
        String message = "";
        if (context instanceof OnlineGame) {
            title = context.getResources().getString(R.string.internet_off);
            message = context.getResources().getString(R.string.play_offline);
            i = new Intent(context, OfflineGame.class);
            i.putExtra("regionsMap", regionsMap);
            i.putExtra("guessRows", guessRows);
            exitGame = 1;

        } else if (context instanceof OfflineGame){
            title = context.getResources().getString(R.string.internet_on);
            message = context.getResources().getString(R.string.back_to_map);
            i = new Intent(context, OnlineGame.class);
            i.putExtra("regionsMap", regionsMap);
            i.putExtra("guessRows", guessRows);
            exitGame = 2;
        }

        else if (context instanceof StartPageSinglePlayer){
            title = context.getResources().getString(R.string.internet_off);
            message = context.getResources().getString(R.string.play_offline);
            i = new Intent(context, OfflineGame.class);
            exitGame = 3;
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title);

        builder.setMessage(message);

        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (exitGame!=3)
                            context.startActivity(i);
                        else
                            StartPageSinglePlayer.playOfflineButton.performClick();
                    }
                }
        );
        builder.setNegativeButton(context.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (exitGame == 1) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        } else {
                            resetDialog.dismiss();
                        }

                    }
                }
        );

        resetDialog = builder.create();
        resetDialog.show();

    }

    public static int emoticon(float score){
        if (score < 25)
            return R.drawable.very_bad;
        else if (score < 50)
            return R.drawable.smiley_bad;
        else if (score < 70)
            return R.drawable.smiley_average;
        else if (score < 90)
            return R.drawable.better;
        else
            return R.drawable.excellent;

    }

    public static TextView customText(Context context){
        TextView title = new TextView(context);
        title.setText(R.string.reset_quiz);
        title.setBackgroundColor(context.getResources().getColor(R.color.wallet_holo_blue_light));
        //title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(context.getResources().getDimension(R.dimen.dimen10dp));

        return title;
    }

    public static void writeHighScore(Context context, double newsc){
        String newHighScore = String.valueOf(newsc);
        SharedPreferences sharedPref = context.getSharedPreferences("highscore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(("highscore"), newHighScore);
        editor.commit();
    }

    public static double readHighScore(Context context){
        SharedPreferences prfs = context.getSharedPreferences("highscore", Context.MODE_PRIVATE);
        try {
            highscore = Double.parseDouble(prfs.getString("highscore", ""));
            return highscore;
        }catch (Exception e){}


        return highscore;
    }

    public static void getCapitals(AssetManager assetManager, HashMap<String, String> capitalsMap)
    {
        InputStream is = null;
        try {
            is = assetManager.open("countriesData.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] RowData = line.split(",");
                String name = RowData[0];
                String capital = RowData[1];
                String population = RowData[2];
                String territory = RowData[3];
                capitalsMap.put(name, capital);
                Log.d("Reading Info", "name: " + name + " population: " + population);
            }

        }
        catch (IOException ex) {
            // handle exception
            Log.d("MyApp", "File not opened in Shared");
        }
        finally {
            try {
                is.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }

    }

    public static HashMap<String, Boolean> getRegionsMap(Context context){
        regionsMap = new HashMap<>();

        String[] regionNms =
                context.getResources().getStringArray(R.array.regionsList);

        for (String region : regionNms)
            regionsMap.put(region, true);
        counter = 0;


        final String[] regionNames =
                regionsMap.keySet().toArray(new String[regionsMap.size()]);

        final boolean[] regionsEnabled = new boolean[regionsMap.size()];
        for (int i = 0; i < regionsEnabled.length; ++i)
            regionsEnabled[i] = regionsMap.get(regionNames[i]);
        final AlertDialog.Builder regionsBuilder =
                new AlertDialog.Builder(context);
        regionsBuilder.setTitle(R.string.regions);

        String[] displayNames = new String[regionNames.length];
        for (int i = 0; i < regionNames.length; ++i)
            displayNames[i] = regionNames[i].replace('_', ' ');




        regionsBuilder.setMultiChoiceItems(
                displayNames, regionsEnabled,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        for (int i = 0; i < regionsEnabled.length; ++i) {
                            if (!regionsEnabled[i])
                                counter++;
                            Log.d("which", " " + counter);
                        }

                        if (counter < 6) {
                            //((AlertDialog) dialog).getListView().setItemChecked(which, false);
                            regionsMap.put(
                                    regionNames[which].toString(), isChecked);
                        } else {
                            ((AlertDialog) dialog).getListView().setItemChecked(which, true);
                        }

                        counter = 0;
                    }
                }
        );

        regionsBuilder.setPositiveButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        //go to the appropriate activity
                    }
                });

        regionsBuilder.setNegativeButton(R.string.cancel,

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {

                    }
                }
        );
        AlertDialog regionsDialog = regionsBuilder.create();
        regionsDialog.show();

        return regionsMap;
    }

    public static void updatePhoto(Activity a, String picturePath, int requestCode, int resultCode, int GALLERY_ACTIVITY_CODE, int RESULT_CROP, Intent data) {
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                picturePath = data.getStringExtra("picturePath");
                Log.d("MyApp", "Filepath: " + picturePath);
                //perform Crop on the Image Selected from Gallery
                SharedMethods.performCrop(picturePath, a, RESULT_CROP);
            }
        }

        if (requestCode == RESULT_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap selectedBitmap = extras.getParcelable("data");
                saveNewPicture(a, selectedBitmap);
            }
        }
    }


    //Saves new profile picture
    public static void saveNewPicture(Activity a, Bitmap selectedBitmap)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        if (ParseUser.getCurrentUser()!=null) {
            File file = new File(path, ParseUser.getCurrentUser().getUsername() + "flagGameProfilePic.jpg"); // the File to save to
            try {
                fOut = new FileOutputStream(file);
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                fOut.flush();
                fOut.close(); // don't forget to close the stream

                MediaStore.Images.Media.insertImage(a.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            } catch (Exception e) {
                Log.d("MyApp", "Exception: " + e.toString());
            }
            a.invalidateOptionsMenu();
        }
    }


    //Crops selected image
    public static void performCrop(String picUri, Activity a, int RESULT_CROP) {
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
            cropIntent.putExtra("outputX", 100);
            cropIntent.putExtra("outputY", 100);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - handle returning in onActivityResult
            a.startActivityForResult(cropIntent, RESULT_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(a, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}
