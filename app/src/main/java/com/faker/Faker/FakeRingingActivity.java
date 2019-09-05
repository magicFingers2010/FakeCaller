package com.faker.Faker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jgabrielfreitas.core.BlurImageView;

import java.io.IOException;

public class FakeRingingActivity extends AppCompatActivity {
    private MediaPlayer mp;
    BlurImageView blurImageView;
    ImageButton answerCall;
    ImageButton rejectCall;
    TextView contactName;
    TextView phoneNumeber;
    TextView timer;
    RelativeLayout defaultLayout;
    RelativeLayout callLayout;
    ImageButton endButton;
    private static final String TAG = "MyActivity";
    final static String PREF_FILE_1 = "pref_file_1";
    MediaPlayer mediaPlayer;
    private boolean startRun;
    private int seconds=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_fake_ringing);

        SharedPreferences shared =getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = shared.edit();
        edit.putString("inProcess","false");
        edit.apply();

        blurImageView = (BlurImageView) findViewById(R.id.BlurImageView);


        timer = (TextView) findViewById(R.id.timer);
        endButton = (ImageButton) findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intents = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intents);
                finish();
                System.exit(0);
            }
        });

        defaultLayout = (RelativeLayout) findViewById(R.id.default_layout);
        callLayout = (RelativeLayout) findViewById(R.id.inCall);
        callLayout.setVisibility(View.GONE);

        String callNumber = getContactNumber();
        String callName = getContactName();
        contactName = (TextView) findViewById(R.id.contactName);
        phoneNumeber = (TextView) findViewById(R.id.contactNumber);
        contactName.setText(callName);
        phoneNumeber.setText(callNumber);
        answerCall = (ImageButton) findViewById(R.id.accept);
        rejectCall = (ImageButton) findViewById(R.id.reject);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        final SharedPreferences sharedPref = this.getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        String defaultValue = "1";
        String highScore = sharedPref.getString("getBackground", defaultValue);
        Log.d(TAG,highScore);
        switch (highScore){
            case "1":
                blurImageView.setImageResource(R.drawable.background2);

                break;
            case "2":
                blurImageView.setImageResource(R.drawable.background1);

                break;
            case "3":
                blurImageView.setImageResource(R.drawable.background3);

                break;
        }

        Timer();

        mp = MediaPlayer.create(getApplicationContext(), notification);
        mp.start();
        answerCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String audioFile = sharedPref.getString("getAudio","error");
                startRun = true;

                if (audioFile.equals("error")){
                    try {
                        AssetFileDescriptor afd = getAssets().openFd("default.m4a");
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(audioFile));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                mp.stop();
                defaultLayout.setVisibility(View.GONE);
                callLayout.setVisibility(View.VISIBLE);

            }
        });
        rejectCall.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mp.stop();
                Intent intents = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intents);
                finish();
                System.exit(0);
            }
        });
    }

    private String getContactNumber(){
        String contact = null;
        try {
            Intent myIntent = getIntent();
            Bundle mIntent = myIntent.getExtras();
            if (mIntent != null) {
                contact = mIntent.getString("myfakenumber");
            }

        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
        return contact;
    }

    private String getContactName(){
        String contactName = null;
        try {

            Intent myIntent = getIntent();
            Bundle mIntent = myIntent.getExtras();
            if (mIntent != null) {
                contactName = mIntent.getString("myfakename");
            }

        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
        return contactName;
    }
    private void Timer(){

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;

                String time = String.format("%d:%02d:%02d", hours, minutes, secs);



                if(startRun){
                    timer.setVisibility(View.VISIBLE);
                    timer.setTextColor(getColor(R.color.white));
                    timer.setText(time);
                    seconds++;
                }

                handler.postDelayed(this, 100);
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mp.stop();
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            mp.stop();
        }catch (Exception e){
            Log.d(TAG,e.getMessage());
        }
    }

}
