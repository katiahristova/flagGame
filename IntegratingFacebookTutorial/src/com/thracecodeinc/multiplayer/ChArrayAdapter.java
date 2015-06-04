package com.thracecodeinc.multiplayer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.thracecodeinc.flagGame.OfflineGame;
import com.thracecodeinc.flagGame.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Samurai on 5/31/15.
 */
public class ChArrayAdapter extends ArrayAdapter<String> {
    private String fromUser;
    private final Context context;
    private final String[] values;
    private final ArrayList<Bitmap> parseFileArray;
    private final String[] senderResult;
    private Button acceptBtn, declineBtn;
    private int[] guessRows;
    private String[] challengerObjId;
    private ArrayList<Map<String, Boolean>> regionsArray;
    private String[] challengeActivityObjId;


    public ChArrayAdapter(Context context, String[] values, ArrayList<Bitmap> userImageMap,
                          String[] senderresult, int[] guessRows,
                          ArrayList<Map<String, Boolean>> regionsArray, String fromUser,
                          String[] challengerObjID, String[] challengeActiviryObjID) {
        super(context, R.layout.challenge_preview_item, values);
        this.context = context;
        this.values = values;
        this.senderResult = senderresult;
        parseFileArray = userImageMap;
        this.guessRows = guessRows;
        this.regionsArray = regionsArray;
        this.fromUser = fromUser;
        this.challengerObjId = challengerObjID;
        this.challengeActivityObjId = challengeActiviryObjID;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.challenge_preview_item, parent, false);
        TextView usrName = (TextView) rowView.findViewById(R.id.user_challenger_name);
        TextView chlngScore = (TextView) rowView.findViewById(R.id.challenger_score);
        TextView chlngRegions = (TextView) rowView.findViewById(R.id.challenger_regions);
        TextView challengesOptions = (TextView) rowView.findViewById(R.id.challenger_options);
        ParseImageView userImage = (ParseImageView) rowView.findViewById(R.id.user_challenge_image);
        acceptBtn = (Button) rowView.findViewById(R.id.accept);


        userImage.setImageBitmap(parseFileArray.get(position));


        challengesOptions.setText(context.getResources().getString(R.string.challenge_options) + " "
                + guessRows[position]*2);
        chlngRegions.setText(context.getResources().getString(R.string.challenge_regions) + ": "
                + extractRegions(position));
        usrName.setText(values[position]);
        chlngScore.setText(context.getResources().getString(R.string.score) + ": " +
                String.format("%.02f%%",Float.parseFloat(senderResult[position])));

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(context, OfflineGame.class);
                resultIntent.putExtra("guessRows", guessRows[position]);
                resultIntent.putExtra("regionsMap", (Serializable) regionsArray.get(position));
                resultIntent.putExtra("startedByUser", true);
                resultIntent.putExtra("multiplayer", false);
                resultIntent.putExtra("fromChallenge", true);
                resultIntent.putExtra("fromuser", values[position]);
                resultIntent.putExtra("challengerResult", senderResult[position]);
                resultIntent.putExtra("challengerobjid", challengerObjId[position]);
                resultIntent.putExtra("challengeActivityObjId", challengeActivityObjId[position]);
                context.startActivity(resultIntent);
            }
        });

        return rowView;
    }

    public String extractRegions(int index){
        String key = "";
        Map<String, Boolean> m = regionsArray.get(index);
        for (Map.Entry<String, Boolean> entry : m.entrySet()) {
            if (entry.getValue())
            key = key+" - "+entry.getKey();

        }
        return key.replace("_"," ");
    }

}

