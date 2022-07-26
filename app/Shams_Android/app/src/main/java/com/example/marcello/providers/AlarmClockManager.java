package com.example.marcello.providers;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;

public class AlarmClockManager {
    private static final String TAG = "AlarmClockManager";
    private static AlarmClockManager instance = new AlarmClockManager();
    final int [] days = { 0, Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY};
    private AlarmClockManager(){
    }
    public static synchronized AlarmClockManager getInstance(){
        return instance;
    }
    public String createAlarmClock(Context context, HashMap<Object, Object> data){

        Log.d(TAG, "createAlarmClock: Dtat = " + data);
        Log.d(TAG, "createAlarmClock: Hour = " + data.get("hour").toString());

        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Calendar cal = Calendar.getInstance();
        double dHour = Double.parseDouble(data.get("hour").toString());
        double dMinute = Double.parseDouble(data.get("minute").toString());
        double dYear = Double.parseDouble(data.get("year").toString());
        double dMonth = Double.parseDouble(data.get("month").toString());
        double dDay = Double.parseDouble(data.get("day").toString());


        int hour = (int) dHour;
        int minute = (int) dMinute;
        int year = (int) dYear;
        int month = (int) dMonth;
        int day = (int) dDay;
        cal.set(year, month - 1, day);
        int nd = cal.get(Calendar.DAY_OF_WEEK);
        alarmIntent.putExtra(AlarmClock.EXTRA_HOUR,  hour);
        alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        alarmIntent.putExtra(AlarmClock.EXTRA_DAYS, days[nd]);
        alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        String result = "تم ضبط المنبه.";
        context.startActivity(alarmIntent);
        return result;
    }
}
