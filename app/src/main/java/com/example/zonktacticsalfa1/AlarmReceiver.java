package com.example.zonktacticsalfa1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String CHANNEL_ID = "alarm_channel_id";

    @Override
    public void onReceive(Context context, Intent ri) {
        String msg = ri.getStringExtra("msg");
        if (msg == null || msg.trim().isEmpty()) {
            msg = "התראה!";
        }
        Log.i(TAG, "onReceive: Alarm activated with message: " + msg);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "התראות אפליקציה",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("ערוץ לקבלת התראות מתוזמנות");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_zonk_alfa)
                .setContentTitle("התראה חדשה")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
