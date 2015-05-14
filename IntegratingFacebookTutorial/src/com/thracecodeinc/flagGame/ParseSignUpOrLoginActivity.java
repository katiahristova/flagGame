package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Katia on 04/26/2015.
 */

public class ParseSignUpOrLoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parse_signup_or_login);

        // Log in button click handler
        ((Button) findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent of the log in activity
                startActivity(new Intent(ParseSignUpOrLoginActivity.this, ParseLoginActivity.class));
            }
        });

        // Sign up button click handler
        ((Button) findViewById(R.id.signup)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(ParseSignUpOrLoginActivity.this, ParseSignUpActivity.class));
            }
        });

        // Log in with Facebook button click handler
        ((Button) findViewById(R.id.login_with_facebook_button)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loginFacebook(getApplicationContext(), ParseSignUpOrLoginActivity.this);
            }
        });

        // Play button click handler
        ((Button) findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Starts an intent for the sign up activity
                startActivity(new Intent(ParseSignUpOrLoginActivity.this, MainActivity.class));
            }
        });
    }

    public static void loginFacebook(final Context c, final Activity a)
    {
        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(a, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Intent intent = new Intent(a, UserDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);

                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                } else {

                    Intent intent = new Intent(a, UserDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                    Log.d("MyApp", "User logged in through Facebook!");

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }
}
