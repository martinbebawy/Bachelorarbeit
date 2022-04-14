package com.example.weathermoodbac;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Notificator {
    private static Calendar randomTimeBetweenBeginAndEndtime;
    private static Calendar begin;
    private static Calendar end;
    private static AlarmManager alarmManager;
    private static PendingIntent pendingIntent;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    private Context ctx;

    public static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "WeatherMoodReminderChannel";
            String description = "Channel für Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ChannelWeatherMood", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void setNotification(Context ctx) {
        alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0); //getBroadcast --> Instanzierung des pendingIntents; requestCode und flags nur bei mehreren Intents notwendig
        alarmManager.set(AlarmManager.RTC_WAKEUP, randomTimeBetweenBeginAndEndtime.getTimeInMillis(), pendingIntent); //AlarmManager löst Broadcast aus zur angegebenen Zeit, AlarmReceiver (Methode onReceive) wird aufgerufen und abgearbeitet
    }


    public static void initBeginAndEndTime() {
        begin = Calendar.getInstance();
        end = Calendar.getInstance();
        begin.set(Calendar.HOUR_OF_DAY, 8);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 24);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
    }

    private static Calendar getRandomTime() {
        Random rnd = new Random();
        initBeginAndEndTime();
        long min = begin.getTimeInMillis();
        long max = end.getTimeInMillis();
        long randomNum = min + rnd.nextLong() % (max - min + 1);
        Calendar res = Calendar.getInstance();
        Log.d("time",sdf.format(res.getTime()));
        res.setTimeInMillis(randomNum);
        res.add(Calendar.DATE, 1);
        return res;
    }

    public static void refreshSharedDataAfterNotification(Context ctx) {
        randomTimeBetweenBeginAndEndtime = getRandomTime();
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(BaseActivity.SHARED_PREFS, ctx.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("NOTIFICATION_TIME", sdf.format(randomTimeBetweenBeginAndEndtime.getTime()));
        editor.commit();
        Log.d("nextNot1",sdf.format(randomTimeBetweenBeginAndEndtime.getTime()));
    }


    public static void initSharedData(Context ctx) {
        if (randomTimeBetweenBeginAndEndtime == null) {
            refreshSharedDataAfterNotification(ctx);
        }
    }

    public static void loadSharedData(Context ctx) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(BaseActivity.SHARED_PREFS, ctx.MODE_PRIVATE);
        try {
            if (sharedPreferences.getString("NOTIFICATION_TIME", "") != "") {
                randomTimeBetweenBeginAndEndtime = Calendar.getInstance();
                randomTimeBetweenBeginAndEndtime.setTime(sdf.parse(sharedPreferences.getString("NOTIFICATION_TIME", "")));
                Log.d("nextNot2",""+sdf.parse(sharedPreferences.getString("NOTIFICATION_TIME", "")));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void cancelNotification(Context ctx) {
        Intent intent = new Intent(ctx, Startseite.class);
        PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


}
