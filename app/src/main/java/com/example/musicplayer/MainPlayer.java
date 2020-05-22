package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

public class MainPlayer extends AppCompatActivity {
    Button play,stop,previous,next;
    SeekBar SongseekBar;
    TextView songText,CurrentTime,TotalTime;
    static MediaPlayer mymediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;
    String sName;
    String time="";

    @SuppressLint("Handlerleak")
    private  Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            CurrentTime.setText(createTimer(msg.what));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_player);
        play = (Button) findViewById(R.id.play);
        previous = (Button) findViewById(R.id.previous);
        next = (Button) findViewById(R.id.next);
        SongseekBar = (SeekBar) findViewById(R.id.seekbar);
        songText = (TextView) findViewById(R.id.SongName);
        CurrentTime = (TextView) findViewById(R.id.startTime);
        TotalTime = (TextView) findViewById(R.id.totalTime);



        updateSeekBar = new Thread(){
            @Override
            public void run() {

                int totalDuration = mymediaPlayer.getDuration();
                int curentPostion = 0;
                while (curentPostion < totalDuration){
                    try {
                        curentPostion = mymediaPlayer.getCurrentPosition();
                        Message message = new Message();
                        message.what = curentPostion;
                        handler.sendMessage(message);
                        sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    SongseekBar.setProgress(curentPostion);

                }


            }
        };




        if (mymediaPlayer != null)
        {
            mymediaPlayer.stop();
            mymediaPlayer.release();
        }

        Intent i =getIntent();
        Bundle bundle = i.getExtras();
        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        sName = mySongs.get(position).getName().toString();

        String SongName = i.getStringExtra("songName");
        songText.setText(SongName);
        songText.setSelected(true);

        position = bundle.getInt("position",0);

        Uri u = Uri.parse(mySongs.get(position).toString());
        mymediaPlayer = MediaPlayer.create(getApplicationContext(),u);
        play.setBackgroundResource(R.drawable.pauseicon);
        mymediaPlayer.start();

        String TotTime = createTimer(mymediaPlayer.getDuration());
        TotalTime.setText(TotTime);
        SongseekBar.setMax(mymediaPlayer.getDuration());

        updateSeekBar.start();



        SongseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mymediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SongseekBar.setMax(mymediaPlayer.getDuration());

                if (mymediaPlayer.isPlaying())
                {
                    play.setBackgroundResource(R.drawable.playicon);
                    mymediaPlayer.pause();
                }
                else
                {
                    play.setBackgroundResource(R.drawable.pauseicon);
                    mymediaPlayer.start();
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mymediaPlayer.stop();
                mymediaPlayer.release();
                position = (position+1)%mySongs.size();

                Uri u = Uri.parse(mySongs.get(position).toString());

                mymediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                sName = mySongs.get(position).getName().toString();
                songText.setText(sName);
                play.setBackgroundResource(R.drawable.pauseicon);
                mymediaPlayer.start();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mymediaPlayer.stop();
                mymediaPlayer.release();
                position = ((position-1) <0)?(mySongs.size()-1):(position-1);
                Uri i = Uri.parse(mySongs.get(position).toString());
                mymediaPlayer = MediaPlayer.create(getApplicationContext(),i);
                sName = mySongs.get(position).getName().toString();
                songText.setText(sName);
                play.setBackgroundResource(R.drawable.pauseicon);
                mymediaPlayer.start();
            }
        });


    }

    public String createTimer(int duration){
        String TimeLabel1 ="";
        int min = duration/1000/60;
        int sec = duration/1000 % 60;

        TimeLabel1 += min + ":";
        if (sec<10) {
            TimeLabel1 += "0";
        }

        TimeLabel1 += sec;
        return TimeLabel1;
    }



}
