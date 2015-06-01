package com.thracecodeinc.multiplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.thracecodeinc.flagGame.GameStartPage;
import com.thracecodeinc.flagGame.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Samurai on 5/31/15.
 */
public class ChArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final ArrayList<ParseFile> parseFileArray;
    private final String[] senderResult;
    private Button acceptBtn, declineBtn;

    public ChArrayAdapter(Context context, String[] values, ArrayList<ParseFile> userImageMap, String[] senderresult) {
        super(context, R.layout.challenge_preview_item, values);
        this.context = context;
        this.values = values;
        this.senderResult = senderresult;
        parseFileArray = userImageMap;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.challenge_preview_item, parent, false);
        TextView usrName = (TextView) rowView.findViewById(R.id.user_challenger_name);
        TextView chlngScore = (TextView) rowView.findViewById(R.id.challenger_score);
        ParseImageView userImage = (ParseImageView) rowView.findViewById(R.id.user_challenge_image);
        acceptBtn = (Button) rowView.findViewById(R.id.accept);

        userImage.setParseFile(parseFileArray.get(position));
        userImage.loadInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {

            }
        });



        usrName.setText(context.getResources().getString(R.string.player) + ": " + values[position]);
        chlngScore.setText(context.getResources().getString(R.string.score) + ": " + senderResult[position]);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Accepted", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, GameStartPage.class);
                context.startActivity(i);
            }
        });

        return rowView;
    }
}

