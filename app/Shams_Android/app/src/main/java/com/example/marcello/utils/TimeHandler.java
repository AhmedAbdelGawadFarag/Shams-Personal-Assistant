package com.example.marcello.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

public class TimeHandler {
    private static final String TAG = "TimeHandler";
    private TimeHandler(){}
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static SimpleTime handle(String hours, String minutes, String format){

        int H = 0, M = 0, F = 0;
        if(hours == null){
            return null;
        }else{
            H = Integer.parseInt(hours);
        }
        if(minutes == null){
            M = 0;
        }else{
            M = Integer.parseInt(minutes);
        }

        if (H > 12 || H < 1){
            return null;
        }
        if(M > 59 || M < 0){
            return null;
        }
        if(format == null || format.equalsIgnoreCase("am")){
            F = 0;
            if(H == 12){
                H = 0;
            }
        }else {
            F = (H < 12 ? 12 : 0);
        }

        H = H + F;

        return new SimpleTime(H, M);
    }
}
