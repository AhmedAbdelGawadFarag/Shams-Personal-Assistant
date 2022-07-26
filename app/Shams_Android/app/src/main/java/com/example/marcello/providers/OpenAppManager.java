package com.example.marcello.providers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class OpenAppManager {
    private static final String TAG = "OpenAppManager";
    private static final OpenAppManager instance = new OpenAppManager();
    private OpenAppManager(){}
    public static synchronized OpenAppManager getInstance(){
        return instance;
    }
    public void openApp(Context context, HashMap<Object, Object> data){
        try{
            final String appName = data.get("appName").toString();
            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo app : apps) {
                if(pm.getLaunchIntentForPackage(app.packageName) != null
                        && !pm.getLaunchIntentForPackage(app.packageName).equals("")) {
                    // apps with launcher intent
                    if((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                        // updated system apps

                    } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        // system apps

                    } else {
                        // user installed apps
                    }
                    if(app.packageName.contains(appName)) {
                        Intent intent = pm.getLaunchIntentForPackage(app.packageName);
                        context.startActivity(intent);
                        break;
                    }
                }

            }
        }catch (Exception e){
            Log.e(TAG, "openApp: " + e.getMessage() );
        }
    }
}
