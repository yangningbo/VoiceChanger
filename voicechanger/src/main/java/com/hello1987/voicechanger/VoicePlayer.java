package com.hello1987.voicechanger;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class VoicePlayer implements OnCompletionListener {

    private static VoicePlayer sInstance = null;

    private MediaPlayer mMediaPlayer;
    private String mCurrentPath;
    private OnVoicePlayListener mOnVoicePlayListener;

    private VoicePlayer() {
    }

    public static synchronized VoicePlayer newInstance() {
        if (sInstance == null) {
            sInstance = new VoicePlayer();
        }

        return sInstance;
    }

    public void startPlaying(String path) {
        if (isPlaying()) {
            stopPlaying();
        }

        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            mCurrentPath = path;
        } catch (Exception e) {
            Log.e("voicechanger", e.getMessage());
        }
    }

    public void stopPlaying() {
        try {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;

            mCurrentPath = null;
        } catch (Exception e) {
            Log.e("voicechanger", e.getMessage());
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer == null) {
            return false;
        }
        return mMediaPlayer.isPlaying();
    }

    public String getCurrentPath() {
        if (isPlaying()) {
            return mCurrentPath;
        }

        return null;
    }

    public int getCurrentPosition() {
        if (isPlaying()) {
            return mMediaPlayer.getCurrentPosition();
        }

        return 0;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopPlaying();
        if (mOnVoicePlayListener != null) {
            mOnVoicePlayListener.onPlayFinished();
        }
    }

    public void setOnVoicePlayListener(OnVoicePlayListener listener) {
        mOnVoicePlayListener = listener;
    }

    public interface OnVoicePlayListener {
        void onPlayFinished();
    }

}
