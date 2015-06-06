package com.thracecodeinc.multiplayer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samurai on 5/26/15.
 */
public class UserDetailQueryAdapter {
    static ParseQueryAdapter.QueryFactory<ParseUser> factory;
    static ParseQueryAdapter.QueryFactory<ParseObject> parseObjectQueryFactory;
    //query that populates the ListView in UserMode
    public static ParseQueryAdapter.QueryFactory<ParseUser> factory(Context contx){
        factory =
                new ParseQueryAdapter.QueryFactory<ParseUser>() {
                    public ParseQuery<ParseUser> create() {

                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.orderByDescending("createdAt");

                        return query;

                    }
                };
        return factory;
    }

    public static ParseQueryAdapter.QueryFactory<ParseObject> gameCompletedQuery(Context contx){
        parseObjectQueryFactory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {
                        ParseQuery<ParseObject> sender = ParseQuery.getQuery("Challenge");
                        sender.whereEqualTo("Sender", ParseUser.getCurrentUser());
                        sender.whereEqualTo("played", true);


                        ParseQuery<ParseObject> receiver = ParseQuery.getQuery("Challenge");
                        receiver.whereEqualTo("Receiver", ParseUser.getCurrentUser());
                        receiver.whereEqualTo("played", true);

                        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                        queries.add(sender);
                        queries.add(receiver);

                        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
                        mainQuery.include("Sender");
                        mainQuery.include("Receiver");
                        mainQuery.orderByDescending("updatedAt");
                        return mainQuery;

                    }
                };

        return parseObjectQueryFactory;
    }

}
