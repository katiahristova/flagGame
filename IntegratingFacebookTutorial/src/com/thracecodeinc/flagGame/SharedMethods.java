package com.thracecodeinc.flagGame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
                         Intent i = new Intent(context, GameStartPage.class);
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

        else if (context instanceof GameStartPage){
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
                            GameStartPage.playOfflineButton.performClick();
                    }
                }
        );
        builder.setNegativeButton(context.getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (exitGame==1) {
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



}
