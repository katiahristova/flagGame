package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private Button loginButton;
    CallbackManager callbackManager;
    ParseUser user;

    List<String> permissions = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        permissions.add("public_profile");
        loginButton = (Button) findViewById(R.id.login_with_facebook_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                user = ParseUser.getCurrentUser();
                if (ParseFacebookUtils.isLinked(user)) {
                    Intent intent1 = new Intent(MainActivity.this, UserDetailsActivity.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    //ParseSignUpOrLoginActivity.loginFacebook(getApplicationContext(), MainActivity.this);
                }
                else {
                    List<String> permissions = new ArrayList<String>();
                    permissions.add("public_profile");
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, MainActivity.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ParseFacebookUtils.isLinked(user)) {
                                //ParseSignUpOrLoginActivity.loginFacebook(getApplicationContext(), MainActivity.this);
                                Log.d("MyApp", "Woohoo, user logged in with Facebook!");
                            }
                            Intent intent = new Intent(MainActivity.this, UserDetailsActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

                }

            }
        });
            }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
                    // Inflate the menu; this adds items to the action bar if it is present.
                    getMenuInflater().inflate(R.menu.start_page_options_menu, menu);
                    MenuItem logout = menu.findItem(R.id.action_settings);

                    if (ParseUser.getCurrentUser() != null) {
                        logout.setTitle(getString(R.string.logout));
                    } else {
                        logout.setTitle(getString(R.string.login));
                    }
                    return true;


                }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            ParseUser.getCurrentUser().logOutInBackground();
            startActivity(new Intent(MainActivity.this, ParseDispatchActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
                protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (resultCode != RESULT_OK) {
                        Log.d("Activity", "Error occured during linking");
                        return;
                    }

                    ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
                    super.onActivityResult(requestCode, resultCode, data);
                }

            }
