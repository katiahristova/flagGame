package com.thracecodeinc.multiplayer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.thracecodeinc.flagGame.R;

/**
 * Created by Samurai on 6/5/15.
 */
public class GameCompletedPreview extends Activity {
    private ParseQueryAdapter<ParseObject> postsQueryAdapter;
    private TextView username;
    private TextView objId;
    private ParseImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.challenge_completed_layout);
        String actionBarTitle = getString(R.string.played_games);
        getActionBar().setTitle(Html.fromHtml("<font color='#20b2aa'>" + actionBarTitle + "</font>"));


        postsQueryAdapter = new ParseQueryAdapter<ParseObject>(GameCompletedPreview.this,
                UserDetailQueryAdapter.gameCompletedQuery(GameCompletedPreview.this)) {

            @Override
            public View getItemView(final ParseObject object, View view, final ViewGroup parent) {
                if (view == null) {
                    view = View.inflate(getContext(), R.layout.challenge_completed_item, null);
                } else{
                }


                TableLayout tl=(TableLayout) view.findViewById(R.id.table_items);

                for (int row = 0; row < tl.getChildCount(); ++row)
                    ((TableRow) tl.getChildAt(row)).removeAllViews();

                LayoutInflater inflater = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                TableRow currentTableRow = (TableRow) tl.getChildAt(0);


                Button challengerScore =
                        (Button) inflater.inflate(R.layout.challenged_completed_btn, null);

                Button challengedScore =
                        (Button) inflater.inflate(R.layout.challenged_completed_btn, null);

                Button winner =
                        (Button) inflater.inflate(R.layout.challenged_completed_btn, null);


                ParseObject sender = object.getParseObject("Sender");
                ParseObject receiver = object.getParseObject("Receiver");

                if (sender != null && receiver != null) {
                    int winnerPoints = 0;
                    String winnerString = "";
                    if (object.getString("winner").equalsIgnoreCase(sender.getString("username"))) {
                        winnerPoints = (int) sender.getNumber("points");
                        winnerString = object.getString("winner");
                    }
                    else if (object.getString("winner").equalsIgnoreCase(receiver.getString("username"))) {
                        winnerPoints = (int) receiver.getNumber("points");
                        winnerString = object.getString("winner");
                    } else {
                        winnerString = "Tie";
                    }

                    challengerScore.setText(sender.getString("username")+"\n"+String.format("%.02f%%",
                            Float.parseFloat(object.getString("senderresult"))));
                    challengedScore.setText(receiver.getString("username")+"\n"+String.format("%.02f%%",
                            Float.parseFloat(object.getString("receiverresult"))));
                    winner.setText(winnerString+"\n"+winnerPoints);

                    currentTableRow.addView(challengerScore);
                    currentTableRow.addView(challengedScore);
                    currentTableRow.addView(winner);
                }




                ParseObject p = object.getParseObject("Sender");
//                username = (TextView) view.findViewById(R.id.username_view);
//                objId = (TextView) view.findViewById(R.id.content_view);
//                userImage = (ParseImageView) view.findViewById(R.id.mainimage);



                if (p != null) {
//                    username.setText(p.getString("username"));
//                    objId.setText(p.getString("gender"));
//
//                    ParseFile fileuserImg = p.getParseFile("userimage");
//                    try {
//                        byte [] file = fileuserImg.getData();
//                        Bitmap imageView = BitmapFactory.decodeByteArray(file, 0, file.length);
//                        userImage.setImageBitmap(imageView);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
                }




                return view;
            }
        };
        final ListView postsListView = (ListView) findViewById(R.id.user_listview);
        postsListView.setAdapter(postsQueryAdapter);
        // Disable automatic loading when the adapter is attached to a view.
        postsQueryAdapter.setAutoload(false);

        // Disable pagination, we'll manage the query limit ourselves
        postsQueryAdapter.setPaginationEnabled(false);
        postsQueryAdapter.loadObjects();

        postsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });

    }
}