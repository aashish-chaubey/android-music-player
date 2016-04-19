package com.sunny.user.soundhound;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.sunny.user.soundhound.R;

import java.io.File;
import java.util.ArrayList;

public class Player extends AppCompatActivity implements View.OnClickListener {

    int position;
    static MediaPlayer mediaPlayer;
    ArrayList<File> mySongs;
    SeekBar seekBar;
    Button play, forward, revind, previous, next;
    Uri uri;
    Thread updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        play = (Button) findViewById(R.id.bt_play);
        forward = (Button) findViewById(R.id.bt_forward);
        revind = (Button) findViewById(R.id.bt_revind);
        previous = (Button) findViewById(R.id.bt_previous);
        next = (Button) findViewById(R.id.bt_next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        play.setOnClickListener(this);
        forward.setOnClickListener(this);
        revind.setOnClickListener(this);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);

        updateSeekBar = new Thread() {
            @Override
            public void run() {
                super.run();
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while (currentPosition < totalDuration) {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songList");
        position = bundle.getInt("pos", 0);

        uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();
        updateSeekBar.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_play :
                if(mediaPlayer.isPlaying()){
                    play.setText(">");
                    mediaPlayer.pause();
                } else {
                    play.setText("||");
                    mediaPlayer.start();
                }
                break;

            case R.id.bt_forward :
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
                break;

            case R.id.bt_revind :
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
                break;

            case R.id.bt_next :
                mediaPlayer.stop();
                mediaPlayer.release();
                position = (position + 1) % mySongs.size();
                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                break;

            case R.id.bt_previous :
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position - 1 < 0) {
                    position = mySongs.size() - 1;
                } else {
                    position -= 1;
                }
                uri = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                break;
        }
    }
}
