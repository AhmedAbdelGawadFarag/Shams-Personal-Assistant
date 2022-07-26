package com.example.marcello.core;

import android.media.MediaPlayer;

import java.io.IOException;

public class MediaPlayerTTS {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private static MediaPlayerTTS instance = new MediaPlayerTTS();
    private MediaPlayerTTS(){}
    public static MediaPlayerTTS getInstance(){
        return instance;
    }
    public void play(String filePath) throws IOException {
        if(mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        if(mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }
    public void nullifyMediaPlayer(){
        this.mediaPlayer = null;
    }

}
