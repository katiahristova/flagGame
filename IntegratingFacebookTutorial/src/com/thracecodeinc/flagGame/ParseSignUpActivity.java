package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Random;


/**
 * Created by Katia on 04/26/2015.
 */
public class ParseSignUpActivity extends Activity {

    private EditText usernameView;
    private EditText passwordView;
    private EditText passwordAgainView;
    private ImageButton setPictureButton;
    private RadioButton maleButton;

    private final int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;
    String picturePath = "";
    Bitmap selectedBitmap = null;
    String gender = "male";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parse_signup_activity);

        // Set up the signup form.
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        passwordAgainView = (EditText) findViewById(R.id.passwordAgain);
        setPictureButton = (ImageButton) findViewById(R.id.set_picture_button);
        maleButton = (RadioButton) findViewById(R.id.radioMale);


        setPictureButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent gallery_Intent = new Intent(getApplicationContext(), GalleryUtil.class);
                startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
            }
        });

        findViewById(R.id.loginTextView).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(ParseSignUpActivity.this, ParseLoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        // Set up the submit button click handler
        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Validate the sign up data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));
                if (isEmpty(usernameView)) {
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                }
                if (isEmpty(passwordView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                }
                if (!isMatching(passwordView, passwordAgainView)) {
                    if (validationError) {
                        validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    validationErrorMessage.append(getResources().getString(
                            R.string.error_mismatched_passwords));
                }
                validationErrorMessage.append(getResources().getString(R.string.error_end));

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(ParseSignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                if (!maleButton.isChecked())
                    gender = "female";

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(ParseSignUpActivity.this);
                dlg.setTitle(getString(R.string.please_wait));
                dlg.setMessage(getString(R.string.logging_in) + " " + getString(R.string.please_wait));
                dlg.show();

                // Set up a new Parse user
                Bitmap usrDefaultImage = BitmapFactory.decodeResource(getResources(),
                        R.drawable.puppy);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                if (selectedBitmap != null)
                    selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                else
                    usrDefaultImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                ParseFile pFile = new ParseFile(randomFIleName(), stream.toByteArray());
                try {
                    pFile.save();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ParseUser user = new ParseUser();
                user.setUsername(usernameView.getText().toString());
                user.setPassword(passwordView.getText().toString());
                user.put("points", 0);
                user.put("userimage", pFile);
                user.put("gender", gender);
                // Call the Parse signup method
                user.signUpInBackground(new SignUpCallback() {

                    @Override
                    public void done(ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(ParseSignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            if (selectedBitmap == null)
                                selectedBitmap = BitmapFactory.decodeResource(getResources(),
                                        R.drawable.puppy);
                            //If a profile picture was set, save that locally
                            if (selectedBitmap != null) {
                                SharedMethods.saveNewPicture(ParseSignUpActivity.this, selectedBitmap);
                                String path = Environment.getExternalStorageDirectory().toString();
                                OutputStream fOut = null;
                                File file = new File(path, ParseUser.getCurrentUser().getUsername() + "flagGameProfilePic.jpg"); // the File to save to
                                try {
                                    fOut = new FileOutputStream(file);
                                    selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                                    fOut.flush();
                                    fOut.close(); // don't forget to close the stream

                                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
                                } catch (Exception e1) {
                                    Log.d("MyApp", "Exception: " + e1.toString());
                                }
                            }

                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(ParseSignUpActivity.this, ParseDispatchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SharedMethods.updatePhoto(this, picturePath, requestCode, resultCode, GALLERY_ACTIVITY_CODE, RESULT_CROP, data);

        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                picturePath = data.getStringExtra("picturePath");
                Log.d("MyApp", "Filepath: " + picturePath);
                Log.d("MyApp", "Pic selected");
                //perform Crop on the Image Selected from Gallery
                SharedMethods.performCrop(picturePath, ParseSignUpActivity.this, RESULT_CROP);
            }
        }

        if (requestCode == RESULT_CROP) {
            Log.d("MyApp", "Pic cropped");
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                selectedBitmap = extras.getParcelable("data");

                if (selectedBitmap != null)
                    setPictureButton.setImageBitmap(selectedBitmap);
            }
        }
    }

    public String randomFIleName() {
    String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rnd = new Random();

        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();

    }
}