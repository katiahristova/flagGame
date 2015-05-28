package com.thracecodeinc.challengeBO;

import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Samurai on 5/28/15.
 */
public class ChallengeBO {
    private ParseUser challengeReceiver;
    private int choices;
    private float challengerResult;
    private float challengeSenderResult;
    private String[] regions;

    public ChallengeBO() {
    }

    public ChallengeBO(ParseUser challengeReceiver, int choices, float challengerResult, 
                       float challengeSenderResult, String[] regions) {
        this.challengeReceiver = challengeReceiver;
        this.choices = choices;
        this.challengerResult = challengerResult;
        this.challengeSenderResult = challengeSenderResult;
        this.regions = regions;
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

    public float getChallengerResult() {
        return challengerResult;
    }

    public void setChallengerResult(float challengerResult) {
        this.challengerResult = challengerResult;
    }

    public float getChallengeSenderResult() {
        return challengeSenderResult;
    }

    public void setChallengeSenderResult(float challengeSenderResult) {
        this.challengeSenderResult = challengeSenderResult;
    }

    public String[] getRegions() {
        return regions;
    }

    public void setRegions(String[] regions) {
        this.regions = regions;
    }
    
    public void doChallengePlayedQuery(){
        ParseObject challenge = new ParseObject("Challenge");
        ParseACL acl = new ParseACL(ParseUser.getCurrentUser());
        acl.setPublicReadAccess(true);
        challenge.put("Sender", ParseUser.getCurrentUser());
        challenge.put("Receiver", this.getChallengeReceiver());
        challenge.put("Choices", this.getChoices());
        challenge.put("regions", this.getRegions());
        challenge.setACL(acl);
        challenge.saveEventually();
        
        
    }
}
