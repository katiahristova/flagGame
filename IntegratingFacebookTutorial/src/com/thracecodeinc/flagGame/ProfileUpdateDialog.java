package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.thracecodeinc.multiplayer.SetupChallenge;
import com.thracecodeinc.multiplayer.UserDetailQueryAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by katiahristova on 6/4/15.
 */
public class ProfileUpdateDialog extends DialogFragment{
        int GALLERY_ACTIVITY_CODE = 200;
        int RESULT_CROP = 400;
        ImageButton pic;
        Button cancelButton, okButton;
        private EditText passwordView;
        private EditText passwordAgainView;
        Bitmap selectedBitmap;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog dialog = new Dialog(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();

            View view = inflater.inflate(R.layout.profile_update_popup, null);

            dialog.setTitle("Update Profile");
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            pic = (ImageButton) view.findViewById(R.id.set_picture_button);
            pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery_Intent = new Intent(getActivity(), GalleryUtil.class);
                    getActivity().startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                }
            });

            passwordView = (EditText) view.findViewById(R.id.password);
            passwordAgainView = (EditText) view.findViewById(R.id.passwordAgain);

            okButton = (Button) view.findViewById(R.id.button_OK);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedBitmap != null) {
                        SharedMethods.saveNewPicture(getActivity(), selectedBitmap);
                        getActivity().invalidateOptionsMenu();
                    }

                    if (!isEmpty(passwordView) || !isEmpty(passwordAgainView)) {
                        // Validate the sign up data
                        boolean validationError = false;
                        StringBuilder validationErrorMessage =
                                new StringBuilder(getResources().getString(R.string.error_intro));

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
                            Toast.makeText(getActivity(), validationErrorMessage.toString(), Toast.LENGTH_LONG)
                                    .show();
                            return;
                        }
                    }

                    // UPDATE INFO ON PARSE HERE

                    dismiss();
                }
            });

            cancelButton = (Button) view.findViewById(R.id.button_cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            dialog.setContentView(view);
            dialog.show();
            return dialog;
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String picturePath = "";

        //SharedMethods.updatePhoto(getActivity(), picturePath, requestCode, resultCode, GALLERY_ACTIVITY_CODE, RESULT_CROP, data);
        if (requestCode == GALLERY_ACTIVITY_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                picturePath = data.getStringExtra("picturePath");
                Log.d("MyApp", "Filepath: " + picturePath);
                Log.d("MyApp", "Pic selected");
                //perform Crop on the Image Selected from Gallery
                setTargetFragment(this, RESULT_CROP);
                SharedMethods.performCrop(picturePath, getActivity(), RESULT_CROP);
            }
        }

        if (requestCode == RESULT_CROP) {
            Log.d("MyApp", "Pic cropped");
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                selectedBitmap = extras.getParcelable("data");

                if (selectedBitmap != null) {
                    pic.setImageBitmap(selectedBitmap);
                }
            }
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    }


