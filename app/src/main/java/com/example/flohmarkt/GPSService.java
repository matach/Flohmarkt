package com.example.flohmarkt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class GPSService extends Service {
    private Thread worker;
    String CHANNEL_ID = "12345";
    NotificationCompat.Builder builder;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("startNewThread"))
            if (!worker.isAlive()) worker.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void doWork() {
        try {
            Toast.makeText(this,"TEST", Toast.LENGTH_LONG).show();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            int notificationId = 1;
            notificationManager.notify(notificationId, builder.build());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        worker.interrupt();
        worker = null;
        super.onDestroy();
    }


}
