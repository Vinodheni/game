package com.myname.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.Random;


public class GameActivity extends Activity {
    static int number = 0, min = 0;
    static int  flag = 0;
    Handler handler;
    Boolean running = true;

    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    public GameFragment mGameFragment;
    public TextView t;
    public String k;
    public Chronometer myc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        t = (TextView) findViewById(R.id.t);
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            number += 1;
                            if (number <= 59)

                                t.setText(String.valueOf(min) + ":" + String.valueOf(number));
                            else {
                                min++;
                                number = 0;
                                t.setText(String.valueOf(min) + ":" + String.valueOf(number));

                            }
                            k = t.getText().toString();
                            flag++;
                            if (flag % 5 == 0)

                            {
                                mGameFragment.func();
                                number = 0;
                            }


                        }
                    });
                }
            }

        };


        new Thread(runnable).start();


        mGameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.game_fragment);


        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);

            if (gameData != null) {

                mGameFragment.putState(gameData);


            }
        }
        Log.d("Ultimate Tic-Tac-Toe", "restore = " + restore);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit().putString(PREF_RESTORE, gameData).commit();

        Log.d("Ultimate Tic-Tac-Toe", "state = " + gameData);
    }

    public void restartGame() {
        mGameFragment.restartGame();
    }

    public void reportWinner(final Tile.Owner winner) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.declare_winner, winner));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        final Dialog dialog = builder.create();
        dialog.show();

        // Reset the board to the initial position
        mGameFragment.initGame();
    }
}