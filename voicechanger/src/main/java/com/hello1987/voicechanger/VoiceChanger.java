package com.hello1987.voicechanger;

import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;
import java.util.List;

public class VoiceChanger {

    private List<short[]> mData = new LinkedList<short[]>();
    private RecordingThread mRecordingThread;
    private SoundTouchThread mSoundTouchThread;

    private OnVoiceChangeListener mListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener == null)
                return;

            int what = msg.what;
            switch (what) {
                case Const.MSG_RECORDING_START:
                case Const.MSG_RECORDING_FAILED:
                case Const.MSG_RECORDING_PAUSED:
                case Const.MSG_RECORDING_FINISHED:
                case Const.MSG_CHANGING_START:
                case Const.MSG_CHANGING_FAILED:
                    mListener.onChangeEvent(new Event(what));
                    break;
                case Const.MSG_RECORDING_UPDATE:
                case Const.MSG_CHANGING_FINISHED:
                    mListener.onChangeEvent(new Event(what, msg.obj));
                    break;
            }
        }
    };

    public VoiceChanger() {
    }

    public void setOnVoiceChangeListener(OnVoiceChangeListener listener) {
        mListener = listener;
    }

    public void startRecording() {
        mData.clear();
        mRecordingThread = new RecordingThread(mHandler, mData);
        mRecordingThread.start();
    }

    public void pauseRecording() {
        if (mRecordingThread != null)
            mRecordingThread.pauseRecording();
    }

    public void stopRecording() {
        if (mRecordingThread != null)
            mRecordingThread.stopRecording();
    }

    public void changeVoice(VoiceType type) {
        mSoundTouchThread = new SoundTouchThread(mHandler, mData, type);
        mSoundTouchThread.start();
    }

    public interface OnVoiceChangeListener {
        void onChangeEvent(Event event);
    }

    public class Event {
        private int type;
        private Object obj;

        public Event(int type) {
            this.type = type;
        }

        public Event(int type, Object obj) {
            this.type = type;
            this.obj = obj;
        }

        public int getType() {
            return type;
        }

        public Object getObject() {
            return obj;
        }
    }

}
