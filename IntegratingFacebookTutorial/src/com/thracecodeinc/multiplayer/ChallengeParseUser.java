package com.thracecodeinc.multiplayer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.thracecodeinc.flagGame.R;

/**
 * Created by katiahristova on 12/16/14.
 */
public class ChallengeParseUser extends DialogFragment {
    private ParseQueryAdapter<ParseUser> postsQueryAdapter;
    private TextView username;
    private TextView objId;
    private ParseImageView userImage;
    public static ParseUser challengedUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());

        dialog.setTitle("Challenge player");
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        final LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.challenge_parse_user, null);


        postsQueryAdapter = new ParseQueryAdapter<ParseUser>(getActivity(),
                UserDetailQueryAdapter.factory(getActivity())) {

            @Override
            public View getItemView(final ParseUser userObj, View view, final ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.challenge_user_item, null);
                } else{
                }

                username = (TextView) view.findViewById(R.id.username_view);
                objId = (TextView) view.findViewById(R.id.content_view);
                userImage = (ParseImageView) view.findViewById(R.id.mainimage);


                username.setText(userObj.getUsername());
                objId.setText(userObj.getString("gender"));

                ParseFile fileuserImg = userObj.getParseFile("userimage");
                try {
                    byte [] file = fileuserImg.getData();
                    Bitmap imageView = BitmapFactory.decodeByteArray(file, 0, file.length);
                    userImage.setImageBitmap(imageView);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                return view;
            }
        };
        final ListView postsListView = (ListView) view.findViewById(R.id.user_listview);
        postsListView.setAdapter(postsQueryAdapter);
        // Disable automatic loading when the adapter is attached to a view.
        postsQueryAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        postsQueryAdapter.setPaginationEnabled(false);
        postsQueryAdapter.loadObjects();

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                challengedUser = postsQueryAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), SetupChallenge.class);
                startActivity(intent);

            }
        });

        dialog.setContentView(view);
        dialog.show();
        return dialog;
    }



}


