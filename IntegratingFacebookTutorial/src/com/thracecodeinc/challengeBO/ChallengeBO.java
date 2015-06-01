package com.thracecodeinc.challengeBO;

import android.content.Context;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.thracecodeinc.multiplayer.ChallengeParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
    private String challengeSender;

    public ChallengeBO() {
        uniqueId = UUID.randomUUID().toString();
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

    public String getChallengeSender() {
        return challengeSender;
    }

    public void setChallengeSender(String challengeSender) {
        this.challengeSender = challengeSender;
    }

    public void setRegions(ArrayList<String> regions) {
        this.regions = regions;
    }

    public void doChallengePlayedQuery(){
        ParseObject challenge = new ParseObject("Challenge");
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        challenge.put("Sender", ParseUser.getCurrentUser());
        challenge.put("Receiver", this.getChallengeReceiver());
        challenge.put("Choices", this.getChoices());
        challenge.put("senderresult", this.getChallengerResult());
        challenge.put("uniqueID", this.getUniqueId());
        challenge.addAll("regions", this.getRegions());
        challenge.setACL(acl);
        challenge.saveEventually();
        
    }

    public void createPushChallenge(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("alert", "Countries Challenge");
            obj.put("fromuser", ParseUser.getCurrentUser().getUsername());
            obj.put("uniquechallengeid", this.getUniqueId());
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


}
