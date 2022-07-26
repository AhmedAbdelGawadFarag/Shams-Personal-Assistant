package com.example.marcello.providers;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmailManager {

    private static final String TAG = "EmailManager";
    
    private static EmailManager instance = new EmailManager();
    private EmailManager(){}
    public static synchronized EmailManager getInstance(){
        return instance;
    }

    /*
    *   @Data:
    *   {
    *      Intent: "intent",
    *      sendTo: "address of the receiver",
    *      subject: "email subject",
    *      body: "body text of the mail"
    *   }
    * */
    public void composeEmail(Context context, HashMap<Object, Object> data){
        Log.d(TAG, "composeEmail: composing an email.");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL,new String[] {data.get("sendTo").toString()});
        intent.putExtra(Intent.EXTRA_SUBJECT, data.get("subject").toString());
        intent.putExtra(Intent.EXTRA_TEXT, data.get("body").toString());
        if(intent.resolveActivity(context.getPackageManager()) != null){
            context.startActivity(intent);
            Log.d(TAG, "composeEmail: opening Gmail.");
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void readMyMails(Context context)  {
        Log.d(TAG, "readMyMails: opening gmail.");
        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
            context.startActivity(intent);
        }catch (Exception e){
            Log.d(TAG, "readMyMails: " + e.getMessage());
        }
    }
}
