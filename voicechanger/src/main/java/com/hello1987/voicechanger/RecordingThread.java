package com.hello1987.voicechanger;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import java.util.List;

public class RecordingThread extends Thread {

    private static final int FREQUENCY = 8000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private Handler mHandler;
    private List<short[]> mData;

    private volatile boolean isStopped = false;
    private volatile boolean isPaused = false;

    public RecordingThread(Handler handler, List<short[]> data) {
        mHandler = handler;
        mData = data;
    }

    public void pauseRecording() {
        isPaused = true;
    }

    public void stopRecording() {
        isStopped = true;
    }

    @Override
    public void run() {
        int bufferSize = AudioRecord.getMinBufferSize(FREQUENCY, CHANNEL,
                ENCODING);
        short[] buffer = new short[bufferSize];
        AudioRecord audioRecord = null;

        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    FREQUENCY, CHANNEL, ENCODING, bufferSize);

            int state = audioRecord.getState();
            if (state != AudioRecord.STATE_INITIALIZED) {
                mHandler.sendEmptyMessage(Const.MSG_RECORDING_FAILED);
            }

            audioRecord.startRecording();
            mHandler.sendEmptyMessage(Const.MSG_RECORDING_START);

            boolean paused = false;
            while (!isStopped) {
                if (isPaused) {
                    if (!paused) {
                        mHandler.sendEmptyMessage(Const.MSG_RECORDING_PAUSED);
                        paused = true;
                    }
                    continue;
                }

                int len = audioRecord.read(buffer, 0, buffer.length);

                if (len == AudioRecord.ERROR_INVALID_OPERATION
                        || len == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }

                long v = 0;
                for (short s : buffer) {
                    v += s * s;
                }

                // double volume = Math.log(v / len);
                double volume = 10 * Math.log10(v / len);
                mHandler.sendMessageDelayed(mHandler.obtainMessage(
                        Const.MSG_RECORDING_UPDATE, volume), 200);

                short[] data = new short[len];
                System.arraycopy(buffer, 0, data, 0, len);

                mData.add(data);
            }

            mHandler.sendEmptyMessage(Const.MSG_RECORDING_FINISHED);
            audioRecord.stop();
        } catch (Exception e) {
            mHandler.sendEmptyMessage(Const.MSG_RECORDING_FAILED);
        } finally {
            if (audioRecord != null) {
                audioRecord.release();
            }
        }
    }
}
