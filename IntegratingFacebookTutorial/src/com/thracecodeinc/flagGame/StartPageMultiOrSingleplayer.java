package com.thracecodeinc.flagGame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

/**
 * Created by katiahristova on 6/3/15.
 */
public class StartPageMultiOrSingleplayer extends FragmentActivity {
    Button multiplayerButton, singleplayerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_or_multiplayer_activity);

        multiplayerButton = (Button) findViewById(R.id.multiplayer_button);
        singleplayerButton = (Button) findViewById(R.id.singleplayer_button);

        singleplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartPageMultiOrSingleplayer.this, StartPageSinglePlayer.class);
                startActivity(i);

            }
        });

        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an intent for the logged in activity
                startActivity(new Intent(StartPageMultiOrSingleplayer.this, StartPageMultiplayer.class));
            }
        });
    }
}
