package com.example.marcello.activities.main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import com.example.marcello.core.BotManager;
import com.example.marcello.core.DialogManager;
import com.example.marcello.core.MediaPlayerTTS;
import com.example.marcello.core.RecordingManager;
import com.example.marcello.api.ApiInterface;
import com.example.marcello.api.RetrofitClient;
import com.example.marcello.activities.main.adapters.ChatMessagesListAdapter;
import com.example.marcello.R;
import com.example.marcello.models.Message;
import com.example.marcello.models.MessageType;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BotManager.ICommandExecution,
        DialogManager.IDialogStatus, BotManager.IUserAudioCommandExecution{

    private final String TAG = "MainActivity";

    // this is the path for storing audio file to be used by TTS
    public static final String STORAGE_EXTERNAL_CACHE_DIR = "/storage/emulated/0/Android/data/com.example.marcello/files/Download/";
    public static final String AUDIO_FILE_NAME = "ttsAudio.mp3";

    private final String [] PERMISSIONS = new String[]{
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.READ_CALENDAR,
      Manifest.permission.WRITE_CALENDAR,
      Manifest.permission.READ_CONTACTS,
      Manifest.permission.WRITE_CONTACTS,
      Manifest.permission.CALL_PHONE,
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.READ_SMS,
      Manifest.permission.RECEIVE_SMS,
      Manifest.permission.READ_PHONE_STATE
    };


    private boolean isOpenDialog = false;
    private boolean isRecording = false;

    private BotManager botManager;
    private DialogManager dialogManager;
    private ActivityResultLauncher<String[]>  permissionsLauncher;


    // widgets
    private ChatMessagesListAdapter messagesAdapter;
    private RecyclerView messagesRecycler;

    // chat array
    private ArrayList<Message> chatList;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.primary_dark));

        // ActivityResultLauncher for requesting multiple permissions
        permissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if(isGranted.containsValue(false)){
                permissionsLauncher.launch(PERMISSIONS);
            }
        });
        askPermissions(permissionsLauncher);

        dialogManager.getInstance().setIDialogStatus(this);


        // setUp BotManager
        botManager = BotManager.getInstance();
        botManager.setICommandExecution(this);
        botManager.setIUserAudioCommandExecution(this);
        // --- ---- --- -- -- --- -
        messagesAdapter = new ChatMessagesListAdapter();
        messagesRecycler =  findViewById(R.id.recycler_view_chat);
        messagesRecycler.setAdapter(messagesAdapter);
        messagesRecycler.setLayoutManager(new LinearLayoutManager(this));

        // init convo with greeting
        chatList= new ArrayList<>();
        chatList.add(new Message("مرحبا! انا شمس مساعدك الافتراضى.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        chatList.add(new Message("سوف احاول تلبيه طلباتك.", Message.MESSAGE_SENDER_BOT, MessageType.TEXT));
        messagesAdapter.setList(chatList);

        ImageButton btnSend =  findViewById(R.id.btn_send_message);
        EditText editTextMessage =  findViewById(R.id.edit_text_message);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.length() == 0){
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                    }else{
                        btnSend.setImageResource(R.drawable.ic_send_message);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                    final String userMsg = editTextMessage.getText().toString();
                    if (userMsg.equals("") && !isRecording) { // start recording
                        isRecording = true;
                        btnSend.setImageResource(R.drawable.ic_baseline_stop_24);
                        Log.d(TAG, "RecordingManager:  started recording...");
                        RecordingManager.getInstance().startRecording();
                        editTextMessage.setEnabled(false);
                    }else if(userMsg.equals("") && isRecording){ // stop recording
                        Log.d(TAG, "RecordingManager: stopped recording.");
                        RecordingManager.getInstance().stopRecording();
                        isRecording = false;
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                        editTextMessage.setEnabled(true);
                        uploadRecordToBeProcessed();
                    }else {
                        chatList.add(new Message(userMsg, Message.MESSAGE_SENDER_USER, MessageType.TEXT));
                        editTextMessage.setText("");
                        uploadTextQueryToBeProcessed(userMsg);
                        btnSend.setImageResource(R.drawable.ic_baseline_mic_24);
                    }

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadRecordToBeProcessed(){
        File audioFile = new File(RecordingManager.STORAGE_EXTERNAL_CACHE_DIR + RecordingManager.AUDIO_FILE_NAME);
        // decode audio file to Base64
        byte[] bytes = null;
        try {
          bytes = FileUtils.readFileToByteArray(audioFile);
        }catch (IOException e){
            Log.d(TAG, "uploadRecordToBeProcessed: " + e.getMessage() );
        }
        if(bytes == null) return;
        String encoded = Base64.encodeToString(bytes, 0);
        try {
            if(!isOpenDialog) {
                botManager.dealWith(getApplicationContext(), encoded, BotManager.QUERY_TYPE_AUDIO);
            }else {
                dialogManager.getInstance().sendMessage(encoded, 1);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadTextQueryToBeProcessed(String query){
        try{
            if(isOpenDialog){
                  dialogManager.getInstance().sendMessage(query , 0);
//                botManager.dealWith(getApplicationContext(), query, BotManager.QUERY_TYPE_FILLING_REQUIREMENTS);
            }else{
                botManager.dealWith(getApplicationContext(), query, BotManager.QUERY_TYPE_TEXT);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onCommandExecutionFinished(Message message) {

        if(message.getMessageText() != null && !message.getMessageText().equals("")) {
            HashMap<Object, Object> payload = new HashMap<>();
            payload.put("text", message.getMessageText());
            payload.put("message", message);
            downloadMP3(payload);
        }else {
            chatList.add(message);
            Log.d(TAG, "BotManager: " + message);
            messagesAdapter.setList(chatList);
        }
    }

    private void askPermissions(ActivityResultLauncher<String[]> permissionsLauncher){
        if(!hasPermissions(PERMISSIONS)) {
            Log.d(TAG, "askPermissions: Some Permissions needs to be granted first.");
            permissionsLauncher.launch(PERMISSIONS);
        }else{
            Log.d(TAG, "askPermissions: All necessary permissions are granted.");
        }
    }
    private boolean hasPermissions(String ... permissions){
        if(permissions != null){
            for(String permission : permissions){
                if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onDialogStarted() {
        this.isOpenDialog = true;
    }

    @Override
    public void onDialogEnded() {
        this.isOpenDialog = false;
        dialogManager = null;
    }

    @Override
    public void onMessageReceived(Message message) {
        chatList.add(message);
        messagesAdapter.setList(chatList);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerTTS.getInstance().nullifyMediaPlayer();
    }

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
                    chatList.add((Message) payload.get("message"));
                    Log.d(TAG, "BotManager: " + (Message) payload.get("message"));
                    messagesAdapter.setList(chatList);
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
            Log.d(TAG, "writeResponseBodyToDisk: Path = " + getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "hi.mp3");
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

    @Override
    public void onUserAudioCommandSent(Message message) {
        chatList.add(message);
        Log.d(TAG, "BotManager: " + message);
        messagesAdapter.setList(chatList);
    }
}

