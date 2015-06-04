package com.thracecodeinc.multiplayer;

import android.app.ListActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thracecodeinc.flagGame.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Samurai on 5/30/15.
 */
public class ChallengePreview extends ListActivity {
    private String[] userNames;
    private ArrayList<Bitmap> userImageArray;
    private String[] senderresult;
    private boolean countriesMode;
    private String fromUser;
    private int[] guessRows;
    private ArrayList<Map<String, Boolean>> regColl;
    private Map<String, Boolean> regionsMap;
    private String[] challengerObjID;
    private String[] challengeActiviryObjID;
    private Bitmap imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userImageArray = new ArrayList<>();


        String actionBarTitle = getString(R.string.challenge_screen);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));

        fromUser = getIntent().getStringExtra("");


        ParseQuery<ParseObject> query = new ParseQuery("Challenge");
        query.include("Sender");
        query.orderByDescending("createdAt");
        query.whereEqualTo("Receiver", ParseUser.getCurrentUser());
        query.whereEqualTo("played", false);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    if (!list.isEmpty()) {
                        regColl = new ArrayList<Map<String, Boolean>>();
                        userNames = new String[list.size()];
                        senderresult = new String[list.size()];
                        guessRows = new int[list.size()];
                        challengerObjID = new String[list.size()];
                        challengeActiviryObjID = new String[list.size()];

                        for (int i = 0; i < list.size(); i++) {
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

                                challengerObjID[i] = list.get(i).getParseObject("Sender").getObjectId();
                                senderresult[i] = list.get(i).getString("senderresult");
                                guessRows[i] = list.get(i).getInt("Choices");
                                challengeActiviryObjID[i] = list.get(i).getObjectId();

                                ParseObject p = list.get(i).getParseObject("Sender");

                                if (p != null) {
                                    userNames[i] = p.getString("username");
                                    ParseFile fileuserImg = p.getParseFile("userimage");
                                    byte [] file = fileuserImg.getData();
                                    if (fileuserImg != null) {
                                        imageView = BitmapFactory.decodeByteArray(file, 0, file.length);
                                        userImageArray.add(imageView);
                                    }
                                }
                                //Toast.makeText(ChallengePreview.this, p.getString("username"),Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(ChallengePreview.this, ex.toString(), Toast.LENGTH_LONG).show();
                            }
                        }


                        setListAdapter(new ChArrayAdapter(ChallengePreview.this, userNames, userImageArray,
                                senderresult, guessRows, regColl, fromUser, challengerObjID, challengeActiviryObjID));
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



    @Override
    protected void onResume() {
        super.onResume();

    }


}
