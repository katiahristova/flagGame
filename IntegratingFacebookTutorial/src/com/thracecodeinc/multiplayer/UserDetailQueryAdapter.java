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

import java.util.List;

/**
 * Created by Samurai on 5/26/15.
 */
public class UserDetailQueryAdapter {
    static Context context;
    static ParseQueryAdapter.QueryFactory<ParseUser> factory;

    //query that populates the ListView in UserMode
    public static ParseQueryAdapter.QueryFactory<ParseUser> factory(Context contx){
        context = contx;
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

}
