package com.hello1987.voicechanger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class WaveHeader {

    public short channels = 1;
    public short sampleRate = 16000;
    public short bitsPerSample = 16;
    private char fileID[] = {'R', 'I', 'F', 'F'};
    private int fileLength;
    private char wavTag[] = {'W', 'A', 'V', 'E'};
    private char fmtHdrID[] = {'f', 'm', 't', ' '};
    private int fmtHdrLeth = 16;
    private short formatTag = 1;
    private short blockAlign = (short) (channels * bitsPerSample / 8);
    private int avgBytesPerSec = blockAlign * sampleRate;

    private char dataHdrID[] = {'d', 'a', 't', 'a'};
    private int dataHdrLeth;

    public WaveHeader(int fileLength) {
        this.fileLength = fileLength + (44 - 8);
        dataHdrLeth = fileLength;
    }

    public WaveHeader(int fileLength, short channels, short sampleRate,
                      short bitsPerSample) {
        this.fileLength = fileLength + (44 - 8);
        dataHdrLeth = fileLength;

        this.channels = channels;
        this.sampleRate = sampleRate;
        this.bitsPerSample = bitsPerSample;

        blockAlign = (short) (channels * bitsPerSample / 8);
        avgBytesPerSec = blockAlign * sampleRate;
    }

    /**
     * @return byte[] 44个字节
     * @throws IOException
     */
    public byte[] getHeader() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        writeChar(bos, fileID);
        writeInt(bos, fileLength);
        writeChar(bos, wavTag);
        writeChar(bos, fmtHdrID);
        writeInt(bos, fmtHdrLeth);
        writeShort(bos, formatTag);
        writeShort(bos, channels);
        writeInt(bos, sampleRate);
        writeInt(bos, avgBytesPerSec);
        writeShort(bos, blockAlign);
        writeShort(bos, bitsPerSample);
        writeChar(bos, dataHdrID);
        writeInt(bos, dataHdrLeth);

        bos.flush();
        byte[] r = bos.toByteArray();
        bos.close();

        return r;
    }

    private void writeShort(ByteArrayOutputStream bos, int s) throws Exception {
        byte[] mybyte = new byte[2];
        mybyte[1] = (byte) ((s << 16) >> 24);
        mybyte[0] = (byte) ((s << 24) >> 24);
        bos.write(mybyte);
    }

    private void writeInt(ByteArrayOutputStream bos, int n) throws Exception {
        byte[] buf = new byte[4];
        buf[3] = (byte) (n >> 24);
        buf[2] = (byte) ((n << 8) >> 24);
        buf[1] = (byte) ((n << 16) >> 24);
        buf[0] = (byte) ((n << 24) >> 24);
        bos.write(buf);
    }

    private void writeChar(ByteArrayOutputStream bos, char[] id) {
        for (int i = 0; i < id.length; i++) {
            char c = id[i];
            bos.write(c);
        }
    }

}
