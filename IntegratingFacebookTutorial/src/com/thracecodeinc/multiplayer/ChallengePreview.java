package com.thracecodeinc.multiplayer;


import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import com.parse.ParseUser;
import com.thracecodeinc.flagGame.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Samurai on 5/30/15.
 */
public class ChallengePreview extends ListActivity {
    private String[] userNames;
    private ArrayList<ParseFile> userImageArray;
    private String[] senderresult;
    private boolean startedByUser;
    private boolean isMultiplayer;
    private boolean countriesMode;
    private boolean isFromChallenge;
    private int guessRows;
    private ArrayList<Map<String, Boolean>> regColl;
    private Map<String, Boolean> regionsMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userImageArray = new ArrayList<>();


        String actionBarTitle = getString(R.string.challenge_screen);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));

        guessRows = getIntent().getIntExtra("guessRows", 1);
        startedByUser = getIntent().getBooleanExtra("startedByUser", false);
        isMultiplayer = getIntent().getBooleanExtra("multiplayer", false);
        isFromChallenge = getIntent().getBooleanExtra("fromChallenge", false);



        ParseQuery<ParseObject> query = new ParseQuery("Challenge");
        query.include("Sender");
        query.orderByDescending("createdAt");
        query.whereEqualTo("Receiver", ParseUser.getCurrentUser());
        //query.orderByDescending("objectId");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        regColl = new ArrayList<Map<String, Boolean>>();
                        userNames = new String[list.size()];
                        senderresult = new String[list.size()];

                        for (int i=0; i<list.size();i++) {
                            try {
                                regionsMap = new HashMap<>();
                                String[] regionNms =
                                        getResources().getStringArray(R.array.regionsList);
                                for (String region : regionNms)
                                    regionsMap.put(region, false);
                                ArrayList<String> regionsFromParse = (ArrayList<String>) list.get(i).get("regions");

                                for (String region : regionsFromParse) {
                                    regionsMap.put(region, true);

                                }

                                regColl.add(regionsMap);

                                senderresult[i] = list.get(i).getString("senderresult");
                                ParseObject p = list.get(i).getParseObject("Sender");

                                if (p != null) {
                                    userNames[i] = p.getString("username");
                                    ParseFile fileuserImg = p.getParseFile("userimage");
                                    if (fileuserImg != null) {
                                        userImageArray.add(fileuserImg);
                                    }
                                }
                                //Toast.makeText(ChallengePreview.this, p.getString("username"),Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(ChallengePreview.this, ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }


                        setListAdapter(new ChArrayAdapter(ChallengePreview.this, userNames, userImageArray,
                                senderresult, guessRows, startedByUser, isMultiplayer, isFromChallenge, regColl));
                    }
                }
            }
        });


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        //get selected items
        String selectedValue = (String) getListAdapter().getItem(position);
        Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();

    }


    public void aquareUserObj() {

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


}
