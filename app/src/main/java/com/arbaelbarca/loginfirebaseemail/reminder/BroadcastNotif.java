package com.arbaelbarca.loginfirebaseemail.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class BroadcastNotif extends BroadcastReceiver {
    Context context;

    public BroadcastNotif(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


    }

    private Calendar getReminderTime(String tglEvent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(tglEvent) - 3);
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }

        Log.d("responCalender", " C " + calendar
                .getTime());

        return calendar;
    }

    public void setAlarmNotif(String tgl) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getReminderIntent(), 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, getReminderTime(tgl).getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_HOUR,
//                AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);

    }

    public void startAt20() {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, getReminderIntent(), 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 1000 * 60 * 10;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 50);
        calendar.set(Calendar.SECOND, 0);
        Log.d("responCalender", " menit " + calendar.getTime());


        /* Repeating on every 20 minutes interval */
//        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
//                1000 * 60 * 01, pendingIntent);


    }

    private Intent getReminderIntent() {
        Intent intent = new Intent(context, BroadcastNotif.class);
        intent.putExtra("type", "Alarm");
        return intent;
    }

}
