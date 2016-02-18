package com.hello1987.voicechanger;

import android.media.AmrInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    public static byte[] shortToByteSmall(short[] buf) {
        byte[] bytes = new byte[buf.length * 2];
        for (int i = 0, j = 0; i < buf.length; i++, j += 2) {
            short s = buf[i];

            byte b1 = (byte) (s & 0xff);
            byte b0 = (byte) ((s >> 8) & 0xff);

            bytes[j] = b1;
            bytes[j + 1] = b0;
        }
        return bytes;
    }

    public static void wav2Amr(String inFile, String outFile) throws Exception {
        InputStream inStream = new FileInputStream(inFile);
        AmrInputStream aStream = new AmrInputStream(inStream);

        File file = new File(outFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStream out = new FileOutputStream(file);

        byte[] x = new byte[1024];
        int len;
        out.write(0x23);
        out.write(0x21);
        out.write(0x41);
        out.write(0x4D);
        out.write(0x52);
        out.write(0x0A);
        while ((len = aStream.read(x)) > 0) {
            out.write(x, 0, len);
        }

        out.close();
        aStream.close();
    }

    public static String generateFilePath() {
        String fileName = String.valueOf(System.currentTimeMillis());
        return generateFilePath(fileName, "amr");
    }

    public static String generateFilePath(String fileName, String fileSuffix) {
        File file = new File(Const.AUDIO_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        return Const.AUDIO_PATH + fileName + "." + fileSuffix;
    }

}
