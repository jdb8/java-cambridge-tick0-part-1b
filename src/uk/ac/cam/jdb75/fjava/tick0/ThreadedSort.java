package uk.ac.cam.jdb75.fjava.tick0;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ThreadedSort {

    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        RandomAccessFile fileA = new RandomAccessFile(f1, "rw");
        RandomAccessFile fileB = new RandomAccessFile(f2, "rw");

        ByteBuffer bbA = ByteBuffer.allocate((int)fileA.length());
        FileChannel channelA = fileA.getChannel();

        for (int i = 0; i < fileA.length()/4; i++){
            bbA.putInt(fileA.readInt());
        }
        fileA.seek(0);
        channelA.write(bbA);
    }

}
