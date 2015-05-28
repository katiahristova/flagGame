package com.thracecodeinc.multiplayer;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Samurai on 5/26/15.
 */
public class UserDetailQueryAdapter {
    static Context context;
    static ParseQueryAdapter.QueryFactory<ParseUser> factory;
    static List<ParseUser> challengeClassList;

    //query that populates the ListView in UserMode
    public static ParseQueryAdapter.QueryFactory<ParseUser> factory(Context contx){
        context = contx;
        factory =
                new ParseQueryAdapter.QueryFactory<ParseUser>() {
                    public ParseQuery<ParseUser> create() {

                        ParseQuery<ParseUser> query = ParseUser.getQuery();




                        return query;

                    }
                };
        return factory;
    }


    public static List<ParseUser> quer(final Context context){
        Toast.makeText(context, "Called ",Toast.LENGTH_SHORT).show();
        ParseQuery<ParseUser> query = ParseUser.getQuery();


        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                challengeClassList  = list;
                Toast.makeText(context, "Inside size "+list.size(),Toast.LENGTH_SHORT).show();
                for (ParseUser l : list) {
                    Toast.makeText(context, "This is "+l.getUsername(),Toast.LENGTH_SHORT).show();
                }
            }
        });
        return challengeClassList;
    }
}
