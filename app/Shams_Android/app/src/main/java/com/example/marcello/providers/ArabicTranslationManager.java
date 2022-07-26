package com.example.marcello.providers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.example.marcello.R;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class ArabicTranslationManager {

    private static ArabicTranslationManager instance = new ArabicTranslationManager();
    private ArabicTranslationManager(){}
    public static synchronized ArabicTranslationManager getInstance(){
        return instance;
    }
    private Translate translate;


    public String translateToArabic(Context context , HashMap<Object, Object> data){
        getTranslateService(context);
        return translate(data.get("sentence").toString(), data.get("targetLanguage").toString());
    }
    private void getTranslateService(Context context) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try (InputStream is = context.getResources().openRawResource(R.raw.credentials)) {

            //Get credentials:
            final GoogleCredentials myCredentials = GoogleCredentials.fromStream(is);

            //Set credentials and get translate service:
            TranslateOptions translateOptions = TranslateOptions.newBuilder().setCredentials(myCredentials).build();
            translate = translateOptions.getService();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private String translate(String originalText, String targetLang) {

        //Get input text to be translated:
        Translation translation = translate.translate(originalText, Translate.TranslateOption.targetLanguage(targetLang), Translate.TranslateOption.model("base"));
        String translatedText = translation.getTranslatedText();
        return translatedText;
    }
}
