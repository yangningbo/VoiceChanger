package com.hello1987.voicechanger;

import android.os.Handler;

import net.surina.soundtouch.SoundTouch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class SoundTouchThread extends Thread {

    private Handler mHandler;
    private List<short[]> mData;
    private VoiceType mType = VoiceType.VT_NONE;

    private List<byte[]> mWavData = new LinkedList<byte[]>();

    public SoundTouchThread(Handler handler, List<short[]> data) {
        this(handler, data, VoiceType.VT_NONE);
    }

    public SoundTouchThread(Handler handler, List<short[]> data, VoiceType type) {
        mHandler = handler;
        mData = data;
        mType = type;
    }

    @Override
    public void run() {
        mHandler.sendEmptyMessage(Const.MSG_CHANGING_START);

        SoundTouch soundtouch = buildSoundTouch();
        for (short[] data : mData) {
            if (data == null) {
                continue;
            }

            soundtouch.putSamples(data, data.length);

            short[] buffer;
            do {
                buffer = soundtouch.receiveSamples();
                byte[] bytes = Utils.shortToByteSmall(buffer);
                mWavData.add(bytes);
            } while (buffer.length > 0);
        }
        soundtouch.release();// 释放资源

        int fileLength = 0;
        for (byte[] bytes : mWavData) {
            fileLength += bytes.length;
        }

        FileOutputStream out = null;
        try {
            WaveHeader waveHeader = new WaveHeader(fileLength);
            byte[] header = waveHeader.getHeader();

            // 保存文件
            String path = Utils.generateFilePath("sound", "wav");
            out = new FileOutputStream(path);
            out.write(header);

            for (byte[] bytes : mWavData) {
                out.write(bytes);
            }

            // 转码
            String outPath = Utils.generateFilePath();
            Utils.wav2Amr(path, outPath);

            mHandler.sendMessage(mHandler.obtainMessage(
                    Const.MSG_CHANGING_FINISHED, outPath));
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(Const.MSG_CHANGING_FAILED);
        } finally {
            if (out != null) {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private SoundTouch buildSoundTouch() {
        SoundTouch soundtouch = new SoundTouch();
        soundtouch.setSampleRate(8000); // 16000
        soundtouch.setChannels(1);

        if (mType == VoiceType.VT_NONE) {
        } else if (mType == VoiceType.VT_KITTY) {
            soundtouch.setRate(1.2f);
            soundtouch.setPitchSemiTones(4);
            soundtouch.setTempoChange(2);
        } else if (mType == VoiceType.VT_ROSE) {
            soundtouch.setRate(1.0f);
            soundtouch.setPitch(2.1f);
        } else if (mType == VoiceType.VT_UNCLE) {
            soundtouch.setRate(1.0f);
            soundtouch.setPitch(0.8f);
        } else if (mType == VoiceType.VT_TOM) {
            soundtouch.setPitchSemiTones(10);
            soundtouch.setRateChange(-0.7f);
            soundtouch.setTempoChange(0.5f);
        }

        return soundtouch;
    }

}
