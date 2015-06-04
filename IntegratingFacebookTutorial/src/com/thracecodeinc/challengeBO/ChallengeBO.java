package com.thracecodeinc.challengeBO;


import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Samurai on 5/28/15.
 */
public class ChallengeBO {
    private ParseUser challengeReceiver;
    private int choices;
    private String challengerResult;
    private float playerChallengedResult;
    private ArrayList<String> regions;
    private String uniqueId;
    private String winner;
    private boolean tieGame;
    private String challengeObjId;
    private String challengeSenderObjId;

    public ChallengeBO() {
        uniqueId = UUID.randomUUID().toString();
        tieGame = false;
    }

    public ChallengeBO(ParseUser challengeReceiver, int choices, String challengerResult,
                       float playerChallengedResult, ArrayList<String> regions) {
        this.challengeReceiver = challengeReceiver;
        this.choices = choices;
        this.challengerResult = challengerResult;
        this.playerChallengedResult = playerChallengedResult;
        this.regions = regions;
        uniqueId = UUID.randomUUID().toString();
    }

    public ParseUser getChallengeReceiver() {
        return challengeReceiver;
    }

    public void setChallengeReceiver(ParseUser challengeReceiver) {
        this.challengeReceiver = challengeReceiver;
    }

    public int getChoices() {
        return choices;
    }

    public void setChoices(int choices) {
        this.choices = choices;
    }

    public String getChallengerResult() {
        return challengerResult;
    }

    public void setChallengerResult(String challengerResult) {
        this.challengerResult = challengerResult;
    }


    public float getPlayerChallengedResult() {
        return playerChallengedResult;
    }

    public void setPlayerChallengedResult(float playerChallengedResult) {
        this.playerChallengedResult = playerChallengedResult;
    }

    public ArrayList<String> getRegions() {
        return regions;
    }

    public String getUniqueId() {
        return uniqueId;
    }



    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }


    public boolean isTieGame() {
        return tieGame;
    }

    public void setTieGame(boolean tieGame) {
        this.tieGame = tieGame;
    }

    public String getChallengeObjId() {
        return challengeObjId;
    }

    public void setChallengeObjId(String challengeObjId) {
        this.challengeObjId = challengeObjId;
    }

    public String getChallengeSenderObjId() {
        return challengeSenderObjId;
    }

    public void setChallengeSenderObjId(String challengeSenderObjId) {
        this.challengeSenderObjId = challengeSenderObjId;
    }
    //***********************************************************************************//

    public void doChallengePlayedQuery(){
        ParseObject challenge = new ParseObject("Challenge");
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        challenge.put("Sender", ParseUser.getCurrentUser());
        challenge.put("Receiver", this.getChallengeReceiver());
        challenge.put("Choices", this.getChoices());
        challenge.put("senderresult", this.getChallengerResult());
        challenge.put("uniqueID", this.getUniqueId());
        challenge.put("played", false);
        challenge.addAll("regions", this.getRegions());
        challenge.setACL(acl);
        challenge.saveEventually();
        
    }

    public void createPushChallenge(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("alert", "Countries Challenge from ");
            obj.put("fromuser", ParseUser.getCurrentUser().getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Find devices associated with these users
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContains("device_id", this.getChallengeReceiver().getObjectId());

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setChannel("ChallengeChanel");
        push.setQuery(pushQuery); // Set our Installation query
        push.setData(obj);
        push.sendInBackground();
    }

    public void challengeCompletedPush(String username){
        JSONObject obj = new JSONObject();
        try {
            obj.put("alert", "completed the challenge!");
            obj.put("fromuser", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Find devices associated with these users
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContains("device_id", this.getChallengeSenderObjId());

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setChannel("ChallengeChanel");
        push.setQuery(pushQuery); // Set our Installation query
        push.setData(obj);
        push.sendInBackground();

    }

    public void challengeCompletedQuery(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");

        query.getInBackground(this.getChallengeObjId(), new GetCallback<ParseObject>() {
            public void done(ParseObject gameScore, ParseException e) {
                if (e == null) {
                    gameScore.put("receiverresult", String.valueOf(getPlayerChallengedResult()));
                    gameScore.put("winner", getWinner());
                    gameScore.put("tie", isTieGame());
                    gameScore.put("played", true);
                    gameScore.saveEventually();
                }
            }
        });
    }

    public void points(String winner){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(winner.trim(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null && user != null) {
                    int points = (int) user.getNumber("points");
                    user.put("points", points + 5);
                    user.saveInBackground();
                }
            }
        });
    }



}
