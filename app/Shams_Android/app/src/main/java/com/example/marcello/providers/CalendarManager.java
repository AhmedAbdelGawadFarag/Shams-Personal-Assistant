package com.example.marcello.providers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class CalendarManager {

    private static final String TAG = "CalendarManager";
    // Projection array. Creating indices for this array instead of doing
    // dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[] {
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

    private static CalendarManager instance = new CalendarManager();
    private CalendarManager() {

    }
    public static synchronized CalendarManager getInstance(){
        return instance;
    }

    /*
    * @Params:
    *
    *
    *
    *
    * */
    public String insertCalendar(Context context, HashMap<Object, Object> data) throws ParseException {

        long calID = 3;
        long startMillis = 0;
        long endMillis = 0;

        for(Map.Entry<Object, Object> i : data.entrySet()){
            Log.d(TAG, "prepare: " + i.getKey() + " -> " + i.getValue());
        }
        // Setting up calender parameters
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.get("startDate").toString()));
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(data.get("endDate").toString()));
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, data.get("title").toString());
        values.put(CalendarContract.Events.DESCRIPTION, data.get("description").toString());
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Africa/Egypt");
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Toast.makeText(context, "Event Id = " + eventID, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "insertCalendar: Event Added to calendar with ID = " + eventID);
        //
        // ... do something with event ID
        //
        //
       return "تم";
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<HashMap<Object, Object>> getEventsOfCalender(Context context, HashMap<Object, Object> data) throws ParseException {
        ContentResolver contentResolver = context.getContentResolver();
        final Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME},
                null, null, null);
        Log.d(TAG, "Cals count = " + cursor.getCount());
        List<HashMap<Object, Object>> res = new ArrayList<>();

        while (cursor.moveToNext()) {

            String calId = cursor.getString(0);
            Log.d(TAG, "getEventsOfCalender: CalID = " + calId);
            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance();
            beginTime.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(data.get("startDate").toString()));
            long startMills = beginTime.getTimeInMillis();

            Calendar endTime = Calendar.getInstance();
            endTime.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(data.get("endDate").toString()));
            endTime.set(endTime.get(Calendar.YEAR), endTime.get(Calendar.MONTH), endTime.get(Calendar.DATE), 23, 59);
            long endMills = endTime.getTimeInMillis();
            Log.d(TAG, "getEventsOfCalender: startTime = " + beginTime.getTime());
            Log.d(TAG, "getEventsOfCalender: endTime = " + endTime.getTime());
            ContentUris.appendId(builder, startMills);
            ContentUris.appendId(builder, endMills);

            Cursor eventCursor = contentResolver.query(builder.build(),
                    new String[]{
                            CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.BEGIN,
                            CalendarContract.Instances.END,
                            CalendarContract.Instances.DESCRIPTION,
                            CalendarContract.Instances._ID},
                    CalendarContract.Instances.CALENDAR_ID + " = ?",
                    new String[]{calId}, null);

            Log.d(TAG, "Events Count = " + eventCursor.getCount());
            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final String description = eventCursor.getString(3);
                final String eventID = eventCursor.getString(4);
                Log.d(TAG, "Title: " + title + "\tDescription: " + description + "\tBegin: " + begin + "\tEnd: " + end + "\tEventID: " + eventID);
                HashMap<Object, Object> event = new HashMap<>();
                event.put("title", title);
                event.put("startDate", begin);
                res.add(event);
            }
        }
        return res;
    }

}
