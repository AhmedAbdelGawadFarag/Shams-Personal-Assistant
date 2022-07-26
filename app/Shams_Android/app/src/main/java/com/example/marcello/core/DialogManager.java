package com.example.marcello.core;


import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import com.example.marcello.api.ApiInterface;
import com.example.marcello.api.RetrofitClient;
import com.example.marcello.models.Message;
import com.example.marcello.models.MessageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialogManager {

    private static final String TAG = "DialogManager";
    public static final String STORAGE_EXTERNAL_CACHE_DIR = "/storage/emulated/0/Android/data/com.example.marcello/files/Download/";
    public static final String AUDIO_FILE_NAME = "ttsAudio.mp3";

    private MediaPlayer mediaPlayer = new MediaPlayer();

    private final ArrayList<String> omitList = new ArrayList<String>()
    {
        {
            add("intent");
        }
    };

    private ArrayList<String> requiredData;
    private ArrayList<String> requiredMessages;
    private HashMap<Object, Object> mData;
    private IDialogStatus mDialogStatus;
    private Context mContext;

    private IDialogResult mDialogResult;

    @SuppressLint("StaticFieldLeak")
    private static DialogManager instance = null;

    private DialogManager(){}
    public static synchronized DialogManager getInstance(){
        if(instance == null){
            instance = new DialogManager();
        }
        return instance;
    }

    /*
    * @Params: Json Object to extract the basic
    * requirements to finish a certain task
    * */
    private void prepare(HashMap<Object, Object> data) {
        for(Map.Entry<Object, Object> i : data.entrySet()){
            Log.d(TAG, "prepare: " + i.getKey() + " -> " + i.getValue());
            if(omitList.contains(i.getKey().toString())) {
                mData.put(i.getKey(), i.getValue());
                continue;
            }
            if(requiredData.contains(i.getKey().toString()) && i.getValue() != null){
                Log.d(TAG, "prepare: Key -> " + i.getKey() + " , Val -> " + i.getValue());
                mData.put(i.getKey(), i.getValue());
                int index = requiredData.indexOf(i.getKey().toString());
                requiredData.remove(index);
                requiredMessages.remove(index);
            }
        }
    }

    /*
    * checks if there still base requirements that haven't been fulfilled and asks them
    * */
    private void requestRequiredData(){
        if(allFulfilled()){
            finish(mContext, mData);
            return ;
        }
        HashMap<Object, Object> payload = new HashMap<>();
        payload.put("text", requiredMessages.get(0));
        downloadMP3(payload);
    }
    /*
    * a way for the user to fulfill the requirements with the dialog manager
    * @logic:
    * -add user's message as it fulfilled a certain requirement
    * -remove that requirement and ask again
    * */
    public void sendMessage(String message, int msgType){
        if(msgType == 0) { // text
            if(message.equals("كنسل")){
                cancel();
                return ;
            }
            mData.put(requiredData.get(0), message);
            requiredData.remove(0);
            requiredMessages.remove(0);
            requestRequiredData();
        }else{ // audio
            ApiInterface client = RetrofitClient.getInstance().create(ApiInterface.class);
            HashMap<Object, Object> payload = new HashMap<>();
            payload.put("audio", message);
            Call<HashMap<Object, Object>> call = client.stt(payload);
            assert call != null;
            Log.d(TAG, "sendMessage: send audio to tts ");
            call.enqueue(new Callback<HashMap<Object, Object>>() {
                @Override
                public void onResponse(Call<HashMap<Object, Object>> call, Response<HashMap<Object, Object>> response) {
                    Log.d(TAG, "upload is success.");
                    Log.d(TAG, "onResponse: " + response.body());

//                  mUserAudioCommandExecution.onUserAudioCommandSent(new Message(response.body().get("userSTT").toString(), Message.MESSAGE_SENDER_USER, MessageType.TEXT));
                    Message messageSTT = new Message(response.body().get("userSTT").toString(), Message.MESSAGE_SENDER_USER, MessageType.TEXT);
                    mDialogResult.onDialogSTT(messageSTT);
                    if(response.body().get("userSTT").equals("كنسل")){
                        cancel();
                        return ;
                    }
                    mData.put(requiredData.get(0), response.body().get("userSTT").toString());
                    requiredData.remove(0);
                    requiredMessages.remove(0);
                    requestRequiredData();
                }
                @Override
                public void onFailure(Call<HashMap<Object, Object>> call, Throwable t) {
                    Log.d(TAG, "upload failed due to: " + t.getMessage());
                }
            });
        }
    }
    /*
    * @Context: for sending it back to the finish method to send results to the BotManager to execute task
    * @Data: the Json data retrieved from server
    * */
    public void start(Context context, HashMap<Object,Object> data,
                      ArrayList<String> REQUIREMENTS, ArrayList<String> MESSAGES){
        requiredData = new ArrayList<>(REQUIREMENTS);
        requiredMessages = new ArrayList<>(MESSAGES);
        this.mDialogStatus.onDialogStarted();
        this.mData = new HashMap<>();
        this.mContext = context;
        prepare(data);
        requestRequiredData();
    }

    // Start dialog that doesn't require any data like "opening an app"
    public void start(Context context, HashMap<Object,Object> data){
        requiredData = new ArrayList<>();
        requiredMessages = new ArrayList<>();
        this.mDialogStatus.onDialogStarted();
        this.mData = new HashMap<>();
        this.mContext = context;
        prepare(data);
        requestRequiredData();
    }
    /*
    * @Usage: alert that the user has done with this dialog and sends the dialog results
    * to BotManager to execute it
    * */
    private void finish(Context context, HashMap<Object, Object> data){
        this.mDialogStatus.onDialogEnded(); // alert that this dialog has ended
        this.mDialogResult.onDialogResults(context, data); // send results to execute the command

    }
    private void askForConfirmation(){}
    private void cancel(){
        this.mDialogStatus.onDialogEnded();
    }
    private boolean allFulfilled(){
        return requiredData.size() == 0;
    }

    public void setIDialogStatus(IDialogStatus dialogStatus){
        this.mDialogStatus = dialogStatus;
    }
    public void setIDialogResult(IDialogResult dialogResult){
        this.mDialogResult = dialogResult;
    }
    public interface IDialogStatus{
         void onDialogStarted();
         void onDialogEnded();
         void onMessageReceived(Message message);
    }
    public interface IDialogResult{
        void onDialogResults(Context context, HashMap<Object, Object> result);
        void onDialogSTT(Message message);
    }

    @SuppressLint("StaticFieldLeak")
    private void downloadMP3(HashMap<Object, Object> payload){
        ApiInterface client = RetrofitClient.getInstance().create(ApiInterface.class);
        Call<ResponseBody> call = client.tts(payload);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: downloadedfile: " + response.body());
                boolean isSuccess = writeResponseBodyToDisk(response.body());
                Log.d(TAG, "onResponse: saving file is successful = " + isSuccess);
                try {
                    Log.d(TAG, "onResponse: playing media");
                    mDialogStatus.onMessageReceived(new Message(requiredMessages.get(0), Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
                    MediaPlayerTTS.getInstance().play(STORAGE_EXTERNAL_CACHE_DIR + AUDIO_FILE_NAME);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: error, couldn't download file.");
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            Log.d(TAG, "writeResponseBodyToDisk: Path = " + mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "hi.mp3");
            Log.d(TAG, "writeResponseBodyToDisk: PATH = " + STORAGE_EXTERNAL_CACHE_DIR + AUDIO_FILE_NAME);
            File futureStudioIconFile = new File(STORAGE_EXTERNAL_CACHE_DIR + AUDIO_FILE_NAME);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
