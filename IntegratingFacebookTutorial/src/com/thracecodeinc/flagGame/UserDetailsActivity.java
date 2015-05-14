package com.thracecodeinc.flagGame;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.model.GameRequestContent;
import com.facebook.share.widget.AppInviteDialog;
import com.facebook.share.widget.GameRequestDialog;
import com.parse.ParseUser;

public class UserDetailsActivity extends Activity {

  private ProfilePictureView userProfilePictureView;
  private TextView userNameView;
  private TextView userGenderView;
  private TextView userEmailView;
  private Button inviteButton, challengeButton;
  ProgressDialog progressDialog;

    GameRequestDialog requestDialog;
    CallbackManager callbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.userdetails);

      userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
      userNameView = (TextView) findViewById(R.id.userName);
      userGenderView = (TextView) findViewById(R.id.userGender);
      userEmailView = (TextView) findViewById(R.id.userEmail);
      inviteButton = (Button) findViewById(R.id.invite_button);
      challengeButton = (Button) findViewById(R.id.challenge_button);

      FacebookSdk.sdkInitialize(this.getApplicationContext());

      callbackManager = CallbackManager.Factory.create();
      requestDialog = new GameRequestDialog(this);
      requestDialog.registerCallback(callbackManager, new FacebookCallback<GameRequestDialog.Result>() {
          public void onSuccess(GameRequestDialog.Result result) {
              //String id = result.getId();
          }

          public void onCancel() {}

          public void onError(FacebookException error) {}
      });

      makeMeRequest();
      GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
          @Override
          public void onCompleted(JSONObject user, GraphResponse response) {
              if (user != null) {
                  userProfilePictureView.setProfileId(user.optString("id"));
              }
          }
      });
      request.executeAsync();


      inviteButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              String appLinkUrl, previewImageUrl;

              appLinkUrl = "https://fb.me/465949803567790";
              previewImageUrl = "http://connectpuzzle.parseapp.com/icon.png";

              if (AppInviteDialog.canShow()) {
                  AppInviteContent content = new AppInviteContent.Builder()
                          .setApplinkUrl(appLinkUrl)
                          .setPreviewImageUrl(previewImageUrl)
                          .build();
                  AppInviteDialog.show(UserDetailsActivity.this, content);
              }
          }
      });

      challengeButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              GameRequestContent content = new GameRequestContent.Builder()
                      .setMessage("Come play this level with me")
                      .build();
              requestDialog.show(content);
          }

      });
  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
  @Override
  public void onResume() {
    super.onResume();

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      // Check if the user is currently logged
      // and show any cached content
      updateViewsWithProfileInfo();
    } else {
      // If the user is not logged in, go to the
      // activity showing the login view.
      //startLoginActivity();

      progressDialog = ProgressDialog.show(UserDetailsActivity.this, "", "Logging in...", true);
      // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
      // (https://developers.facebook.com/docs/facebook-login/permissions/)
      ParseSignUpOrLoginActivity.loginFacebook(getApplicationContext(), UserDetailsActivity.this);
      progressDialog.dismiss();
      updateViewsWithProfileInfo();
    }

  }


  private void makeMeRequest() {
    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
            new GraphRequest.GraphJSONObjectCallback() {
              @Override
              public void onCompleted(JSONObject user, GraphResponse response) {
                if (user != null) {
                  // Create a JSON object to hold the profile info
                  JSONObject userProfile = new JSONObject();
                  try {
                    // Populate the JSON object

                    userProfile.put("id", user.optString("id"));
                    userProfile.put("name", user.optString("name"));
                    if (user.optString("gender") != null) {
                      userProfile.put("gender", user.optString("gender"));
                    }
                    if (user.optString("email") != null) {
                      userProfile.put("email", user.optString("email"));
                    }

                    // Save the user profile info in a user property
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.put("profile", userProfile);
                    currentUser.saveInBackground();

                    // Show the user info
                    updateViewsWithProfileInfo();
                  } catch (JSONException e) {
                    Log.d(flagGameApplication.TAG, "Error parsing returned user data. " + e);
                  }

                } else if (response.getError() != null) {
                  Log.d(flagGameApplication.TAG, "The facebook session was invalidated." + response.getError());
                    logout();
                  } else {
                    Log.d(flagGameApplication.TAG,
                            "Some other error: " + response.getError());
                  }
                }

            }
    );
    request.executeAsync();
  }

  private void updateViewsWithProfileInfo() {
    ParseUser currentUser = ParseUser.getCurrentUser();
    JSONObject userProfile = null;
    if (currentUser.has("profile")) {
      userProfile = currentUser.getJSONObject("profile");


      try {

        if (userProfile.has("id")) {
          userProfilePictureView.setProfileId(userProfile.optString("id"));
          //userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
        } else {
          // Show the default, blank user profile picture
          userProfilePictureView.setProfileId(null);
        }

        if (userProfile.has("name")) {
          userNameView.setText(userProfile.getString("name"));
          currentUser.put("name", userProfile.getString("name"));
        } else {
          userNameView.setText("");
        }

        if (userProfile.has("gender")) {
          userGenderView.setText(userProfile.getString("gender"));
          currentUser.put("gender", userProfile.getString("gender"));
        } else {
          userGenderView.setText("");
        }

        if (userProfile.has("email")) {
          userEmailView.setText(userProfile.getString("email"));
          currentUser.put("email", userProfile.getString("email"));
        } else {
          userEmailView.setText("");
        }
        currentUser.put("profile", userProfile);
        currentUser.saveInBackground();

      } catch (JSONException e) {
        Log.d(flagGameApplication.TAG, "Error parsing saved user data.");
      }
    }
    else {
      Log.d("MyApp", "User has no profile.");
    }

  }

  public void onLogoutClick(View v) {
    logout();
  }

  private void logout() {
    // Log the user out

    ParseUser.logOutInBackground();

    // Go to the login view
    startLoginActivity();
  }


  private void startLoginActivity() {Intent intent = new Intent(this, ParseSignUpOrLoginActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent); }

}
