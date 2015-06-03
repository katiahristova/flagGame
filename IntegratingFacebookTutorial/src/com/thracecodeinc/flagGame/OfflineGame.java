package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.thracecodeinc.challengeBO.ChallengeBO;
import com.thracecodeinc.multiplayer.ChallengeParseUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Samurai on 4/19/15.
 */
public class OfflineGame extends Activity {
    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList;
    private Map<String, Boolean> regionsMap;
    public static HashMap<String, String> capitalsMap = new HashMap<String, String>();
    private String correctAnswer;
    private int totalGuesses; // total number of guesses made
    private int questionsPassed; // number of questions passed (guessed correct or timer ended)
    private int correctGuesses; //number of correct guesses
    private int incorrectGuessesForQuestion; //number of incorrect guesses for a particular question

    private int guessRows, numberOfQuestions, timerSeconds;
    private Random random;
    private Handler handler;
    private Animation shakeAnimation;
    private String[] regionNames;
    private TextView answerTextView;
    private TextView questionNumberTextView, timerView;
    private ImageView flagImageView;
    private TableLayout buttonTableLayout;
    private boolean startedByUser;
    private boolean isMultiplayer;
    private boolean countryMode;
    private boolean isFromChallenge;
    private String fromUser;
    private String challngResultFromParse;

    Timer T;
    Runnable myRunnable;
    int count;

    @Override

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flags_offline_game);

        String actionBarTitle = getString(R.string.guess_country);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));

        SharedPreferences prefs = OfflineGame.this.getSharedPreferences(
                "com.thracecodeinc.falgGame", Context.MODE_PRIVATE);
        countryMode = prefs.getBoolean("countryMode", true);
        numberOfQuestions = prefs.getInt("numberOfQuestions", 10);

        guessRows = getIntent().getIntExtra("guessRows", guessRows);
        setTimerSeconds(guessRows);

        Toast.makeText(getApplicationContext(),"guessrows "+guessRows,Toast.LENGTH_LONG);
        fileNameList = new ArrayList<String>();
        quizCountriesList = new ArrayList<String>();
        regionsMap = new HashMap<String, Boolean>();

        guessRows = getIntent().getIntExtra("guessRows", 1);
        startedByUser = getIntent().getBooleanExtra("startedByUser", false);
        isMultiplayer = getIntent().getBooleanExtra("multiplayer", false);
        isFromChallenge = getIntent().getBooleanExtra("fromChallenge", false);
        fromUser = getIntent().getStringExtra("fromuser");
        challngResultFromParse = getIntent().getStringExtra("challengerResult");

        timerView = (TextView) findViewById(R.id.timerTextView);

        if (isMultiplayer || isFromChallenge)
            timerView.setVisibility(View.VISIBLE);

        random = new Random();
        handler = new Handler();
        shakeAnimation =
                AnimationUtils.loadAnimation(this, R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        regionNames =
            getResources().getStringArray(R.array.regionsList);
        for (String region : regionNames )
            regionsMap.put(region, true);
        regionsMap.putAll((HashMap<String,Boolean>) getIntent().getSerializableExtra("regionsMap"));

        SharedMethods.getCapitals(getAssets(), capitalsMap);

        questionNumberTextView =
                (TextView) findViewById(R.id.questionNumberTextView);
        flagImageView = (ImageView) findViewById(R.id.flagImageView);
        buttonTableLayout =
                (TableLayout) findViewById(R.id.buttonTableLayout);
        answerTextView = (TextView) findViewById(R.id.answerTextView);
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " 1 " +
                        getResources().getString(R.string.of) + " " + numberOfQuestions);

        resetQuiz();
    }

    private void resetQuiz()
    {
        if (SharedMethods.isOnline(OfflineGame.this) && !startedByUser)
            SharedMethods.networkModePopup(OfflineGame.this, (HashMap) regionsMap, guessRows);

        AssetManager assets = getAssets();
        fileNameList.clear();

        try
        {
            Set<String> regions = regionsMap.keySet();

            for (String region : regions)
            {
                if (regionsMap.get(region))
                {   String[] paths = assets.list(region);
                    for (String path : paths) {
                        fileNameList.add(path.replace(".png", ""));
                    }
                }
            }
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Error loading image file names", e);
        }

        questionsPassed = 0;
        totalGuesses = 0;
        correctGuesses = 0;
        quizCountriesList.clear();

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();
        while (flagCounter <= numberOfQuestions)
        {
            int randomIndex = random.nextInt(numberOfFlags);
            String fileName = fileNameList.get(randomIndex);
            if (!quizCountriesList.contains(fileName))
            {
                quizCountriesList.add(fileName);
                ++flagCounter;
            }}

        loadNextFlag();
    }
    private void loadNextFlag()
    {
        incorrectGuessesForQuestion = 0;

        String nextImageName = quizCountriesList.remove(0);
        correctAnswer = nextImageName;

        answerTextView.setText("");
        questionNumberTextView.setText(
                getResources().getString(R.string.question) + " " +
                        (questionsPassed + 1) + " " +
                        getResources().getString(R.string.of) + " " + numberOfQuestions);
        String region =
                nextImageName.substring(0, nextImageName.indexOf('-'));
        AssetManager assets = getAssets(); // get app's AssetManager
        InputStream stream;
        try
        {
            stream = assets.open(region + "/" + nextImageName + ".png");

            Drawable flag = Drawable.createFromStream(stream, nextImageName);
            flagImageView.setImageDrawable(flag);
        }
        catch (IOException e)
        {
            //Log.e(TAG, "Error loading " + nextImageName, e);
        }
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
            ((TableRow) buttonTableLayout.getChildAt(row)).removeAllViews();

        Collections.shuffle(fileNameList);


        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);


        for (int row = 0; row < guessRows; row++)
        {
            TableRow currentTableRow = getTableRow(row);

            for (int column = 0; column < 2; column++)
            {
                Button newGuessButton =
                        (Button) inflater.inflate(R.layout.flags_guess_button, null);
                String fileName = fileNameList.get((row * 2) + column);
                if (countryMode)
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
        if (countryMode)
            ((Button)randomTableRow.getChildAt(column)).setText(getCountryNameFromStrings(this, correctAnswer));
        else
            ((Button)randomTableRow.getChildAt(column)).setText(capitalsMap.get(correctAnswer.substring(correctAnswer.indexOf("-") + 1)));

        if (isMultiplayer || isFromChallenge)
        {   count = 0;

            T=new Timer();
            myRunnable = new Runnable() {
                @Override
                public void run() {
                    timerView.setText("0"+ ((timerSeconds-1)-count/100)+ ":" + (100-count%100));
                    count++;
                    if (count == (timerSeconds*100) ) {
                        T.cancel();
                        timerView.setText("00:00");
                        //++totalGuesses;
                        ++questionsPassed;
                        totalGuesses += (guessRows - incorrectGuessesForQuestion);
                        if (questionsPassed < numberOfQuestions)
                            loadNextFlag();
                    }
                }
            };
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(myRunnable);
                }
            }, 10, 10);

        }
    }
    private TableRow getTableRow(int row)
    {
        return (TableRow) buttonTableLayout.getChildAt(row);
    }
    private String getCountryName(String name)
    {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }
    private void submitGuess(Button guessButton)
    {
        String guess = guessButton.getText().toString();
        String answer = getCountryNameFromStrings(this, correctAnswer);
        ++totalGuesses;
        String country = correctAnswer.substring(correctAnswer.indexOf("-")+1);
        //Log.d("MyApp", "Guess: " + guess + ", " + answer + ", " + capitalsMap.get(answer));
        if (guess.equals(answer) || guess.equals(capitalsMap.get(country)))
        {
            if (isMultiplayer || isFromChallenge) {
                T.cancel();
                timerView.setText("00:00");
            }
            ++questionsPassed;
            ++correctGuesses;
            answerTextView.setText(answer);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.correct_answer));
            guessButton.setTextColor(getResources().getColor(R.color.correct_answer));
            disableButtons();
            if (questionsPassed == numberOfQuestions) {
                endOfGame();
            }
            else
            {  handler.postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            loadNextFlag();
                        }
                    }, 1000);
            }
        }
        else
        {
            incorrectGuessesForQuestion++;
            flagImageView.startAnimation(shakeAnimation);
            answerTextView.setText(R.string.incorrect_answer);
            answerTextView.setTextColor(
                    getResources().getColor(R.color.incorrect_answer));
            guessButton.setEnabled(false);
            guessButton.setTextColor(getResources().getColor(R.color.translucent_black));
        }
    }

    //Called when a game is finishing
    private void endOfGame()
    {
        double currentHighScore = SharedMethods.readHighScore(OfflineGame.this);
        float scorePrcntg = correctGuesses*100 / (float) totalGuesses;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.results);
        builder.setIcon(SharedMethods.emoticon(scorePrcntg));

        //builder.setTitle(R.string.new_score);

        if (currentHighScore < scorePrcntg) {

            SharedMethods.writeHighScore(OfflineGame.this, scorePrcntg);
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

        if (isMultiplayer) {
            createChallenge(scorePrcntg);
        } else if (isFromChallenge){
            challengeCompleted(scorePrcntg);
        } else {
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.reset_quiz,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            isMultiplayer = false;
                            timerView.setVisibility(View.INVISIBLE);
                            resetQuiz();
                        }
                    }
            );
            AlertDialog resetDialog = builder.create();
            resetDialog.show();
        }



    }
    private void disableButtons()
    {
        for (int row = 0; row < buttonTableLayout.getChildCount(); ++row)
        {
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
    private View.OnClickListener guessButtonListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            submitGuess((Button) v);
        }
    };

    public void resetGamePopup(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCustomTitle(SharedMethods.customText(getApplicationContext()));

        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetQuiz();
                    }
                }
        );
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog resetDialog = builder.create();
        resetDialog.show();
    }

    public static String getCountryNameFromStrings(Activity a, String fileName)
    {
        int resId = a.getResources().getIdentifier(fileName.substring(fileName.indexOf("-") + 1), "string", a.getPackageName());
        Log.d("Country 1", "Country: " + fileName.substring(fileName.indexOf("-") + 1));
        return a.getString(resId);

    }



    public void createChallenge(float result){
        String strValueResult = String.valueOf(result);
        ArrayList<String> regionsArray = new ArrayList<>();
        for(Map.Entry<String, Boolean> map : regionsMap.entrySet()){
            if (map.getValue()){
                regionsArray.add(map.getKey());
            }
        }
        ChallengeBO challengeBO = new ChallengeBO();
        challengeBO.setRegions(regionsArray);
        challengeBO.setChoices(guessRows);
        challengeBO.setChallengeReceiver(ChallengeParseUser.challengedUser);
        challengeBO.setChallengerResult(strValueResult);
        challengeBO.doChallengePlayedQuery();
        challengeBO.createPushChallenge();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getResources().getString(R.string.challenge_sent)+" "+
                challengeBO.getChallengeReceiver().getUsername()+" "+
                (getResources().getString(R.string.was_sent)));
        builder.setMessage(String.format("%d %s, %.02f%% %s",
                totalGuesses, getResources().getString(R.string.guesses),
                (result),
                getResources().getString(R.string.correct)));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isMultiplayer = false;
                        timerView.setVisibility(View.INVISIBLE);
                        Intent i = new  Intent(OfflineGame.this, StartPageMultiOrSingleplayer.class);
                        startActivity(i);
                    }
                }
        );
        AlertDialog resetDialog = builder.create();
        resetDialog.show();

    }

    public void challengeCompleted(float result){

        ChallengeBO challengeBO = new ChallengeBO();
        challengeBO.setChallengeSender(fromUser);
        challengeBO.setChallengerResult(challngResultFromParse);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (result > Float.parseFloat(challngResultFromParse))
            builder.setTitle("You Won");
        else if (result == Float.parseFloat(challngResultFromParse))
            builder.setTitle("Tie Game");
        else
            builder.setTitle("You Lost");

        builder.setMessage(String.format("%d %s, %.02f%% %s",
                totalGuesses, getResources().getString(R.string.guesses),
                (result),
                getResources().getString(R.string.correct)) + "\nChallenger's score " +
                String.format("%.02f%%",Float.parseFloat(challngResultFromParse)));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isMultiplayer = false;
                        timerView.setVisibility(View.INVISIBLE);
                        Intent i = new Intent(OfflineGame.this, StartPageMultiOrSingleplayer.class);
                        startActivity(i);
                        finish();
                    }
                }
        );
        AlertDialog resetDialog = builder.create();
        resetDialog.show();
    }


    //Sets the timer to a different number of seconds depending on the number of choices
    private void setTimerSeconds(int guesses)
    {
        switch (guesses)
        {
            case 1:
                timerSeconds = 5;
                break;
            case 2:
                timerSeconds = 10;
                break;
            case 3:
                timerSeconds = 15;
                break;
        }
    }
    @Override
    public void onBackPressed() {
        SharedMethods.quitGamePopup(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isMultiplayer)
            finish();
    }

}
