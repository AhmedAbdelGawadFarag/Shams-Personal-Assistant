package com.example.marcello.core;

import android.media.AudioFormat;
import android.util.Log;

import com.github.squti.androidwaverecorder.WaveRecorder;

public class RecordingManager {

    private static final String TAG = "RecodingManager";

    public static final String STORAGE_EXTERNAL_CACHE_DIR = "/storage/emulated/0/Android/data/com.example.marcello/files/Download/";
    public static final String AUDIO_FILE_NAME = "output.wav";

    private static RecordingManager instance = new RecordingManager();
    private WaveRecorder recorder;

    private RecordingManager(){}

    public static synchronized RecordingManager getInstance() {
        return instance;
    }
    public void startRecording(){
        if(recorder == null) {
            configRecorder();
        }
        recorder.startRecording();
    }
    public void stopRecording(){
        if(recorder == null)return;
        recorder.stopRecording();
    }
    private void configRecorder(){
        recorder = new WaveRecorder(STORAGE_EXTERNAL_CACHE_DIR + AUDIO_FILE_NAME);
        recorder.getWaveConfig().setSampleRate(44100);
        recorder.getWaveConfig().setChannels( AudioFormat.CHANNEL_IN_MONO);
        recorder.getWaveConfig().setAudioEncoding(AudioFormat.ENCODING_PCM_16BIT);
    }

}
