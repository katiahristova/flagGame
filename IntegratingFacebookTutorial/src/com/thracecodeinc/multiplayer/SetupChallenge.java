package com.thracecodeinc.multiplayer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thracecodeinc.flagGame.OfflineGame;
import com.thracecodeinc.flagGame.R;
import com.thracecodeinc.flagGame.SharedMethods;

import java.util.HashMap;

import bolts.Bolts;

/**
 * Created by Samurai on 5/27/15.
 */
public class SetupChallenge extends FragmentActivity {
    private Button countryChallenge;
    private Button selectRegionsButton;
    private HashMap<String, Boolean> regionsMap;
    private int guessRows;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_challenge);

        String actionBarTitle = getString(R.string.multiplayer);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));

        regionsMap = new HashMap<>();

        countryChallenge = (Button) findViewById(R.id.countryBtn);
        selectRegionsButton = (Button) findViewById(R.id.buttonSelectRegions);


        selectRegionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regionsMap = SharedMethods.getRegionsMap(SetupChallenge.this);
            }
        });

        countryChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), OfflineGame.class);
                i.putExtra("startedByUser",true);
                i.putExtra("multiplayer", true);
                showSelectNumberOfChoicesPopup(i);
            }
        });

    }

    public void showSelectNumberOfChoicesPopup(final Intent i)
    {

        final String[] possibleChoices =
                getResources().getStringArray(R.array.guessesList);

        AlertDialog.Builder choicesBuilder =
                new AlertDialog.Builder(SetupChallenge.this);
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
}
