package com.thracecodeinc.flagGame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.thracecodeinc.multiplayer.SetupChallenge;
import com.thracecodeinc.multiplayer.UserDetailQueryAdapter;

/**
 * Created by katiahristova on 6/4/15.
 */
public class ProfileUpdateDialog extends DialogFragment{
        int GALLERY_ACTIVITY_CODE = 200;

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
            ImageButton pic = (ImageButton) view.findViewById(R.id.set_picture_button);
            pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gallery_Intent = new Intent(getActivity(), GalleryUtil.class);
                    startActivityForResult(gallery_Intent, GALLERY_ACTIVITY_CODE);
                }
            });



            dialog.setContentView(view);
            dialog.show();
            return dialog;
        }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //SharedMethods.updatePhoto(this, picturePath, requestCode, resultCode, GALLERY_ACTIVITY_CODE, RESULT_CROP, data);

    }

    }


