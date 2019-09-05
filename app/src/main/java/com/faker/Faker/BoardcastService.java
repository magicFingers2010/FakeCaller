package com.faker.Faker;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class BoardcastService extends Service {
    private final static String TAG = "MyActivity";
    private final static Integer TEST = 200;
    long countDownTime;
    long notificationTimer = 0;

    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    private static final String CHANNEL_DEFAULT_IMPORTANCE = "TIMER";
    Intent bi = new Intent(COUNTDOWN_BR);
    int notificationId = 200;
    Boolean set = false;

    CountDownTimer cdt = null;
    private NotificationManager notificationManager;



    @Nullable
    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(notificationId,getNotification(" "));
        Log.d(TAG, "Starting timer...");


    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.d(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        countDownTime = intent.getLongExtra("time",0);
        Starter();
        createNotificationChannel();
        return START_REDELIVER_INTENT;

    }
    private void Starter(){
        if (set.equals(false)) {
            notificationManager = getSystemService(NotificationManager.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        cdt = new CountDownTimer(countDownTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                Log.d(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                notificationTimer = millisUntilFinished;
               try {
                   Notification notification = getNotification("Time Remaining Until Fake Call" + " " + (int) millisUntilFinished / 1000 +"s");
                    notificationManager.notify(notificationId, notification);
                    set = true;
                }catch (Exception e){
                   Log.d(TAG,e.getMessage());
               }
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                notificationManager.cancelAll();
            }
        };

        cdt.start();


    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_DEFAULT_IMPORTANCE, name, importance);
            channel.setDescription(description);
           notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private String convertSeconds(long seconds) {
        String mSeconds = String.valueOf(seconds / 1000);
        return mSeconds +"s";
    }
    private String convertMinutes(long seconds){
        int mDo = (int) seconds / 1000;
        String mMinutes = String.valueOf(mDo / 60);
        return mMinutes;
    }
    private Notification getNotification(String text){
        Intent notificationIntent = new Intent(this, inProcessActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return  new NotificationCompat.Builder(this,CHANNEL_DEFAULT_IMPORTANCE)
                .setContentTitle(getText(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_access_alarms_black_24dp)
                .setContentIntent(pendingIntent)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

    }
}
