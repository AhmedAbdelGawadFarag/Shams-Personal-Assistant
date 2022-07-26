package com.example.marcello.providers;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;

public class WebSearchManager {

    private static WebSearchManager instance = new WebSearchManager();
    private WebSearchManager(){}

    public static synchronized WebSearchManager getInstance() {
        return instance;
    }
    public String doSearch(Context context,  HashMap<Object, Object> data){

        Intent webSearchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
        webSearchIntent.putExtra(SearchManager.QUERY, data.get("searchQuery").toString());
        webSearchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final android.os.Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                context.startActivity(webSearchIntent);
            }
        }, 1000);
        return "هذه النتائج التى وجدتها.";
    }
}
