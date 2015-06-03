package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class OnlineGame extends FragmentActivity {
    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList;
    private Map<String, Boolean> regionsMap;
    public static HashMap<String, String> capitalsMap = new HashMap<String, String>();
    private String answer;
    private String correctAnswer;
    private int totalGuesses; // number of guesses made
    private int correctAnswers; // number of correct guesses
    private int guessRows;
    private Random random;
    private Handler handler;
    private Animation shakeAnimation;
    private Drawable flag;
    //private TextView answerTextView;
    private TextView questionNumberTextView;
    private TableLayout buttonTableLayout;
    private GoogleMap mMap;
    private LatLng latLng;
    private CustomInfoWindowForMarker customInfoWindowForMarker;
    private String nextImageName;
    private AssetManager assetManager;
    private Button newGuessButton;
    private Button nextButton;
    private boolean firstRun;
    private boolean correctAnswerGiven = false;

    private boolean countriesMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_online_game);
        String actionBarTitle = getString(R.string.guess_country);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));

        countriesMode = false;
        firstRun = true;
        customInfoWindowForMarker = new CustomInfoWindowForMarker();
        fileNameList = new ArrayList<String>();
        quizCountriesList = new ArrayList<String>();
        regionsMap = new HashMap<String, Boolean>();

        SharedMethods.getCapitals(getAssets(), capitalsMap);

        guessRows = getIntent().getIntExtra("guessRows", 2);
        random = new Random();
        handler = new Handler();
        shakeAnimation =
                AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);
        String[] regionNames =
                getResources().getStringArray(R.array.regionsList);
        for (String region : regionNames)
            regionsMap.put(region, true);
        regionsMap.putAll((HashMap<String,Boolean>) getIntent().getSerializableExtra("regionsMap"));

        assetManager = getAssets();
        customInfoWindowForMarker.getPopulations(assetManager);

        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        buttonTableLayout =
                (TableLayout) findViewById(R.id.buttonTableLayout);
        //ne = (TextView) findViewById(R.id.answerTextView);
        nextButton = (Button) findViewById(R.id.buttonNext);
        nextButton.setOnClickListener(new OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              if (correctAnswerGiven) {
                                                  correctAnswerGiven = false;
                                                  mMap.clear();
                                                  mMap.setInfoWindowAdapter(null);
                                                  loadNextFlag();
                                                  nextButton.setVisibility(View.INVISIBLE);
                                                  if (latLng != null)
                                                  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                                                  latLng, (float) 5.00));
                                                          showNextFlagMarker();
                                              }
                                          }
                                      }
        );
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " 1 " +
                        getResources().getString(R.string.of) + " 10");

        resetQuiz();
        setUpMapIfNeeded();
    }

    private void resetQuiz() {

        assetManager = getAssets();
        fileNameList.clear();

        try {
            Set<String> regions = regionsMap.keySet();

            for (String region : regions) {
                if (regionsMap.get(region)) {
                    String[] paths = assetManager.list(region);

                    for (String path : paths)
                        fileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException e) {
            Log.e("Guess The Flag", "Error loading image file names", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();
        while (flagCounter <= 10) {
            int randomIndex = random.nextInt(numberOfFlags);
            String fileName = fileNameList.get(randomIndex);
            if (!quizCountriesList.contains(fileName)) {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }
        }
        Collections.shuffle(quizCountriesList);
        loadNextFlag();

        handler.postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(0,0), (float) 5.00), new GoogleMap.CancelableCallback() {

                            @Override
                            public void onFinish() {
                                mMap.clear();
                                mMap.setInfoWindowAdapter(null);
                                Bitmap bitmap = ((BitmapDrawable)flag).getBitmap();
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(0,0))
                                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                            }

                            @Override
                            public void onCancel() {
                                Log.d("animation", "onCancel");
                            }
                        });



                    }
                }, 1000);

    }

    private void loadNextFlag() {


        if (quizCountriesList.size() > 0)
            nextImageName = quizCountriesList.remove(0);
        correctAnswer = nextImageName;

        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " " +
                        (correctAnswers + 1) + " " +
                        getResources().getString(R.string.of) + " 10");

        String region =
                nextImageName.substring(0, nextImageName.indexOf('-'));
        assetManager = getAssets(); // get app's AssetManager
        InputStream stream;
        try {
            stream = assetManager.open(region + "/" + nextImageName + ".png");
            flag = Drawable.createFromStream(stream, nextImageName);

        } catch (IOException e) {
            //Log.e(TAG, "Error loading " + nextImageName, e);
        }
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

        Collections.shuffle(fileNameList);

        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);


        for (int row = 0; row < guessRows; row++) {
            TableRow currentTableRow = getTableRow(row);

            for (int column = 0; column < 2; column++) {
                newGuessButton =
                        (Button) inflater.inflate(R.layout.flags_guess_button, null);
                String fileName = fileNameList.get((row * 2) + column);
                //Set button text to country name from string resource files
                if (countriesMode)
                    newGuessButton.setText(getCountryNameFromStrings(this, fileName));
                else
                    newGuessButton.setText(capitalsMap.get(fileName.substring(fileName.indexOf("-") + 1)));

                newGuessButton.setOnClickListener(guessButtonListener);
                currentTableRow.addView(newGuessButton);
            }
        }
        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        TableRow randomTableRow = getTableRow(row);
        if (countriesMode)
            ((Button)randomTableRow.getChildAt(column)).setText(getCountryNameFromStrings(this, correctAnswer));
        else
            ((Button)randomTableRow.getChildAt(column)).setText(capitalsMap.get(correctAnswer.substring(correctAnswer.indexOf("-")+1)));

    }

    private TableRow getTableRow(int row) {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }

    private String getCountryName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    private void submitGuess(Button guessButton) {
        String guess = guessButton.getText().toString();
        answer = getCountryName(getCountryNameFromStrings(this,correctAnswer));
        ++totalGuesses;
        String country = correctAnswer.substring(correctAnswer.indexOf("-") + 1);
        //Log.d("MyApp", "Guess: " + guess + ", " + country + ", " + capitalsMap.get(country));
        if (guess.equals(answer)  || guess.equals(capitalsMap.get(country))) {
            correctAnswerGiven = true;
            guessButton.setTextColor(getResources().getColor(R.color.correct_answer));
            new GeocoderTask().execute(getCountryName(correctAnswer));
            ++correctAnswers;

            disableButtons();

        } else {
            guessButton.setTextColor(getResources().getColor(R.color.translucent_black));
            guessButton.startAnimation(shakeAnimation);
            guessButton.setEnabled(false);
        }
    }

    private void disableButtons() {
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row) {
            TableRow tableRow = (TableRow) buttonTableLayout.getChildAt(row);
            for (int i = 0; i < tableRow.getChildCount(); ++i)
                tableRow.getChildAt(i).setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flags_options_menu_myactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_return_home:
                SharedMethods.quitGamePopup(this);
                break;
            case R.id.menu_new_game:
                resetGamePopup();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private OnClickListener guessButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            submitGuess((Button) v);
        }
    };


    /**
     * **********************MAp Starts HEre**************************************
     */////
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
            }

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {

                }
            });

        }
    }

    public void showNextFlagMarker(){
        mMap.clear();
        Bitmap bitmap = ((BitmapDrawable)flag).getBitmap();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .snippet("")
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))).hideInfoWindow();

    }


    private void setUpMap(LatLng latLng) {
        mMap.setInfoWindowAdapter(new CustomInfoWindowForMarker(this, nextImageName, flag));
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();

    }

    public static String getCountryNameFromStrings(Activity a, String fileName)
    {
        int resId = a.getResources().getIdentifier(fileName.substring(fileName.indexOf("-") + 1), "string", a.getPackageName());
        Log.d("Country 1", "Country: " + fileName.substring(fileName.indexOf("-") + 1));
        return a.getString(resId);

    }





    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
                if (addresses.size()!=0)
                    Log.d("adress ","1"+addresses.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (!SharedMethods.isOnline(getApplicationContext()))
                SharedMethods.networkModePopup(OnlineGame.this, (HashMap) regionsMap, guessRows);
            else {
                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(getBaseContext(), R.string.no_location_found, Toast.LENGTH_SHORT).show();

                    if (firstRun) {
                        new GeocoderTask().execute(answer.trim());
                        firstRun = false;
                    } else resetQuiz();
                }

                // Clears all the existing markers on the map
                mMap.clear();


                Address address;
                // Adding Markers on Google Map for each matching address
                for (int i = 0; i < addresses.size(); i++) {

                    address = addresses.get(i);
                    Log.d("adress ", "2" + address);

                    // Creating an instance of GeoPoint, to display in Google Map
                    latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Locate the first location
                    if (i == 0)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, (float) 5.00), new GoogleMap.CancelableCallback() {

                            @Override
                            public void onFinish() {
                                Log.d("animation", "onFinishCalled");
                                //setUpMap(latitude, longtitude);

                                setUpMap(latLng);

                                if (correctAnswers == 10) {
                                    handler.postDelayed(
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    gameFinishedPopup();
                                                }
                                            }, 1000);

                                } else nextButton.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCancel() {
                                Log.d("animation", "onCancel");
                            }
                        });
                }
            }
          }
        }



    public void gameFinishedPopup(){
        nextButton.setVisibility(View.INVISIBLE);
        //just a little delay between the old game and the new game
        double currentHighScore = SharedMethods.readHighScore(OnlineGame.this);
        float scorePrcntg = 1000 / (float) totalGuesses;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.results);
        builder.setIcon(SharedMethods.emoticon(scorePrcntg));


        if (currentHighScore < scorePrcntg) {
            SharedMethods.writeHighScore(OnlineGame.this, scorePrcntg);
            builder.setMessage(String.format("%d %s, %.02f%% %s \n %s - %.02f%%",
                    totalGuesses, getResources().getString(R.string.guesses),
                    (scorePrcntg),
                    getResources().getString(R.string.correct),
                    getResources().getString(R.string.new_score), scorePrcntg));
        } else{
            builder.setMessage(String.format("%d %s, %.02f%% %s",
                    totalGuesses, getResources().getString(R.string.guesses),
                    (scorePrcntg),
                    getResources().getString(R.string.correct)));
        }

        builder.setCancelable(true);
        builder.setPositiveButton(R.string.reset_quiz,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetQuiz();
                    }
                }
        );
        AlertDialog resetDialog = builder.create();
        resetDialog.show();

    }

    public void resetGamePopup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCustomTitle(SharedMethods.customText(getApplicationContext()));

        builder.setCancelable(true);
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetQuiz();
                    }
                }
        );
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog resetDialog = builder.create();
        resetDialog.show();
    }

    @Override
    public void onBackPressed() {
        SharedMethods.quitGamePopup(this);
    }


}
