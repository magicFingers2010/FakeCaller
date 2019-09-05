package com.faker.Faker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class inProcessActivity extends AppCompatActivity {


    private final static String TAG = "MyActivity";
    final static String PREF_FILE_1 = "pref_file_1";
    private TextView progressText;
    private ProgressBar progressBar;
    private ImageButton back;

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent);
            Log.e(TAG, String.valueOf(2000));// or whatever method used to update your GUI fields
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_process);

        progressText = (TextView) findViewById(R.id.progress);
        SharedPreferences sharedPref =getSharedPreferences(PREF_FILE_1, Context.MODE_PRIVATE);
        String inProgress = sharedPref.getString("inProcess","0");

        assert inProgress != null;
        if (inProgress.equals("false")){
            Intent intents = new Intent(this, MainActivity.class);
            startActivity(intents);
            finish();
        }
        long time = getIntent().getLongExtra("TRY",7);
        Log.e(TAG, String.valueOf(time));
        if (!isMyServiceRunning(BoardcastService.class)){
            boardCast(time);
        }else{
            registerReceiver(br, new IntentFilter(BoardcastService.COUNTDOWN_BR));
            Log.d(TAG, "Registered broacast receiver");
        }



    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(BoardcastService.COUNTDOWN_BR));
        Log.d(TAG, "Registered broacast receiver");
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.d(TAG, "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }


    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            String text = String.valueOf(millisUntilFinished / 1000) + "s";
            progressText.setText(text);
            Log.d(TAG, "Countdown seconds remaining: " + millisUntilFinished );
        }
    }
    public void boardCast(long time){
        Intent serviceIntent = new Intent(this, BoardcastService.class);
        serviceIntent.putExtra("time",time);
        Log.d(TAG, String.valueOf(time));
        startForegroundService(serviceIntent);
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
