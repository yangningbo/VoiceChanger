package com.hello1987.voicechanger;

import android.os.Environment;

public class Const {

    public static final int MSG_RECORDING_START = 1;
    public static final int MSG_RECORDING_FAILED = 2;
    public static final int MSG_RECORDING_UPDATE = 3;
    public static final int MSG_RECORDING_PAUSED = 4;
    public static final int MSG_RECORDING_FINISHED = 5;
    public static final int MSG_CHANGING_START = 6;
    public static final int MSG_CHANGING_FAILED = 7;
    public static final int MSG_CHANGING_FINISHED = 8;
    private static final String ROOT_PATH = Environment
            .getExternalStorageDirectory().toString();
    public static final String AUDIO_PATH = ROOT_PATH + "/audio/";

}
