package com.faker.Faker;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.faker.Faker.dialog.nameDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import pub.devrel.easypermissions.EasyPermissions;


/**
 * A simple {@link Fragment} subclass.
 */
public class audioSettings extends AppCompatActivity implements nameDialog.NoticeDialogListener {

    View mainView;
    ImageButton stopButton;
    LinearLayout stop;
    ImageButton record;
    ImageButton list;
    TextView audioTimer;
    ImageButton mic;
    private boolean startRun;
    private int seconds=0;
    private String folder;
    private MediaRecorder recorder;
    private static String fileName;
    private static final String TAG = "MyActivity";
    private static int RECORD = 1;
    boolean noFiles;
    private String filepath;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_settings);
        requirePermissions();
        initialize();
        Timer();


    }

    private void initialize() {
        stopButton = (ImageButton) findViewById(R.id.stopButton);
        stop = (LinearLayout) findViewById(R.id.stop);
        stopButton.getBackground().setAlpha(128);
        record = (ImageButton) findViewById(R.id.record);
        list = (ImageButton) findViewById(R.id.list);
        stop.setEnabled(false);

        if (!haveFiles()){
            list.getBackground().setAlpha(100);
            noFiles = true;
        }
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noFiles){
                    Toast.makeText(getApplication(),"You have No recordings Yet, Click on The Red Button To Record!",Toast.LENGTH_LONG).show();
                }else {
                    Fragment audioList = new audioList();
                    loadFragmentFromright(audioList);
                }
            }
        });
        audioTimer = (TextView) findViewById(R.id.timer_status);
        audioTimer.setVisibility(View.GONE);
        mic = (ImageButton) findViewById(R.id.mic);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasAudioDevice()){
                    if (recordAudio().equals(false)){
                        startRun = false;

                    }else{
                        startRun = true;
                        stop.setEnabled(true);
                        v.setEnabled(false);
                        list.setEnabled(false);
                        noFiles = false;
                        list.getBackground().setAlpha(100);
                        v.getBackground().setAlpha(100);
                        stopButton.getBackground().setAlpha(225);
                    }


                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(){
                    @Override
                    public void run() {
                        try{

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    v.setEnabled(false);
                                    Fragment audioList = new audioList();
                                    loadFragmentFromright(audioList);
                                    record.setEnabled(true);
                                    list.setEnabled(true);
                                    startRun = false;
                                    recorder.stop();
                                    recorder.release();
                                    recorder = null;
                                    record.getBackground().setAlpha(255);
                                    list.getBackground().setAlpha(255);
                                    mic.setVisibility(View.VISIBLE);
                                    audioTimer.setVisibility(View.GONE);
                                    audioTimer.setText(" ");
                                }
                            });
                        }catch (Exception e){
                            Log.d(TAG,e.getMessage());
                        }
                    }
                }.start();



                v.setEnabled(false);
            }
        });


    }



    private Boolean hasAudioDevice(){
        PackageManager pmanager = getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
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
                    mic.setVisibility(View.GONE);
                    audioTimer.setVisibility(View.VISIBLE);
                    audioTimer.setText(time);
                    seconds++;
                }

                handler.postDelayed(this, 100);
            }
        });

    }
    private Boolean recordAudio() {
        Boolean ad = true;
        folder = Environment.getExternalStorageDirectory() + File.separator + "Faker/";
        fileName = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
         filepath = folder + fileName+".aac";
        File directory = new File(folder);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        recorder.setOutputFile(filepath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.prepare();
            recorder.start();
        }catch (Exception e){
            Toast.makeText(this,"A sound Recorder app is open in the Background ,Please close it and try later",Toast.LENGTH_LONG).show();
            ad = false;

        }
        return ad;
    }
    private void requirePermissions() {
        String[] perms = {
                Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this,"Please Allow this permissions to make the app function properly",
                    RECORD, perms);
        }
    }
    private boolean haveFiles(){
            String path = Environment.getExternalStorageDirectory() + File.separator + "Faker/";
            Log.d("Files", path);
        File f = new File(path);

            if (!f.exists()) {
            return false;
            }
            File[] file = f.listFiles();
            if (String.valueOf(file.length).equals("0")){
                return false;
            }else{
                return true;
            }

    }
    public void loadFragmentFromright(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(String.valueOf(fragment));
        transaction.commit();
    }


    @Override
    public void onDialogPositiveClick(String dialog) {

    }

    @Override
    public void onDialogNegativeClick(String dialog) {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
