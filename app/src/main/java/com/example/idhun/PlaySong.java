package com.example.idhun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release(); //used to delete instance
        updateSeek.interrupt(); //stopping the Thread
    }

    TextView textView;
    ImageView play, previous, next;
    ArrayList<File> songs;
    String textContent;
    MediaPlayer mediaPlayer;
    int position;
    SeekBar seekBar;
    Thread updateSeek;
    int currentPosition=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.pause);
        previous= findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        textView.setSelected(true);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras(); //through bundle, now you can get all the values sent through "putExtra()" by MainActivity class
        songs = (ArrayList)bundle.getParcelableArrayList("songList"); //type casting the returned data to ArrayList as "getParcelableArrayList()" function returns ParcelableArrayList as data.
        textContent = bundle.getString("currentSong");
        textView.setText(textContent);
        position = bundle.getInt("position");

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        seekBar.setMax(mediaPlayer.getDuration());
        mediaPlayer.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    play.setImageResource(R.drawable.play); //
                }
                else {
                    mediaPlayer.start();
                    play.setImageResource(R.drawable.pause);
                }
            }

        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!=songs.size()-1){
                    position++;
                }
                else position = 0 ;
                play.setImageResource(R.drawable.pause);
                textView.setText(songs.get(position).getName().replace(".mp3",""));
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration()); //need to setMax of each song
                mediaPlayer.start();
                seekBar.setProgress(0);
                currentPosition=0; //making the seekBar go at start
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position!=0){
                    position--;
                }
                else position = songs.size()-1 ;
                play.setImageResource(R.drawable.pause);
                textView.setText(songs.get(position).getName().replace(".mp3",""));
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.start();
                seekBar.setProgress(0);
                currentPosition=0;
            }

        });
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
        updateSeek = new Thread(){ //it'll update seekBar live
            @Override
            public void run() { //overriding run()
                currentPosition = 0;
                try {
                    while (currentPosition<mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
    }
}