package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyService extends Service implements SensorEventListener {
    private final Handler mHandler = new Handler();
    private SensorManager sensorManager;
    private float changeValueX,changeValueY,changeValueZ;
    boolean isHeld=true;
    float a=0,b=0,c=0,x=0,y=0,z=0;
    int delay = 5000;
    SharedVariable sharedVariable;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedVariable = new SharedVariable(this);
        sharedVariable.clearNotificationList();

        SharedPreferences sharedPreferences0 = getSharedPreferences("isParentShared", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("mood", Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences2 = getSharedPreferences("notificationList", Context.MODE_PRIVATE);

        //Log.d("MyService", "This statement is executed repeatedly");
        //600000
        //sharedVariable.setNotificationList("NotIn");
        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(this, delay);
                delay = 5000;
                //Log.d("MyService", "This statement is executed repeatedly");

                x = changeValueX;
                y = changeValueY;
                z = changeValueZ;
                if (!isHeld) {
                    if (a + 1 < x || a - 1 > x || b + 1 < y || b - 1 > y || c + 1 < z || c - 1 > z) {
                        Log.d("MainActivity", "Phone is in hand");
                        NotificationHandler.notifiList.add("Phone is in hand @" + getCTime());
                        delay = 600000; //30000
                        isHeld = true;
                        Log.d("New Notification List", String.valueOf(NotificationHandler.notifiList));
                    }
                } else {
                    if (a + 0.03f > x && a - 0.03f < x && b + 0.03f > y && b - 0.03f < y && c + 0.03f > z && c - 0.03f < z) {
                        Log.d("MainActivity", "Phone is not in hand");
                        isHeld = false;
                        //sharedVariable.setNotificationList("NotIn");
                    }
                }
                a = changeValueX;
                b = changeValueY;
                c = changeValueZ;
            }
        };

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mRunnable.run();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //mHandler.postDelayed(mRunnable, 1000); // 1 second delay
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event){
        changeValueX = event.values[0];
        changeValueY = event.values[1];
        changeValueZ= event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    private String getCTime(){
        Date now = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        return dateFormat.format(now);
    }

}


