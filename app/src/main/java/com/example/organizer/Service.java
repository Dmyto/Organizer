package com.example.organizer;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

public class Service extends IntentService {

    public Service() {
        super("name");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, alarmIntent,0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,0,10000, pendingIntent);
    }
}
