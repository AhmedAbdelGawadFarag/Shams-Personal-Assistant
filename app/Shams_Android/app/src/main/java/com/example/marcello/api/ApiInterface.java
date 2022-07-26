package com.example.marcello.api;

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("create_calendar")
    public Call<HashMap<Object, Object>> createCalendar();

    @GET("read_events")
    public Call<HashMap<Object, Object>> getEvents();

    @GET("update_calendar")
    public Call<HashMap<Object, Object>> updateEvent();

    @GET("read_contacts")
    public Call<HashMap<Object, Object>> readContacts();

    @GET("add_contact")
    public Call<HashMap<Object, Object>> addContact();

    @GET("delete_contact")
    public Call<HashMap<Object, Object>> deleteContact();

    @GET("make_call")
    public Call<HashMap<Object, Object>> makeCall();

    @GET("web_search")
    public Call<HashMap<Object, Object>> webSearch();

//    @POST("upload/audio")
    @POST("upload")
    Call<HashMap<Object, Object>> uploadAudio(@Body HashMap<Object, Object> audio);

    @POST("upload")
    Call<HashMap<Object, Object>> uploadText(@Body HashMap<Object, Object> text);

    @GET("test")
    Call<HashMap<Object, Object>> test();

    @GET("openmail")
    Call<HashMap<Object,Object>> opanMail();

    @GET("composeMail")
    Call<HashMap<Object, Object>> composeMail();

    @GET("openApp")
    Call<HashMap<Object, Object>> openApp();

    @GET("readNotification")
    Call<HashMap<Object, Object>> readNotification();

    @POST("tts")
    Call<ResponseBody> tts(@Body HashMap<Object, Object> audio);

    @POST("stt")
    Call<HashMap<Object, Object>> stt(@Body HashMap<Object, Object> text);


}
