package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;

public class TestReadSpeed {

    RandomAccessFile file1;
    RandomAccessFile file2;
    DataInputStream dis;
    DataOutputStream dos;

    @Before
    public void setUp() throws Exception {
        this.file1 = new RandomAccessFile("example1read", "rw");
        this.file2 = new RandomAccessFile("example2read", "rw");
        this.dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file1.getFD())));
        this.dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file1.getFD())));

        for (int i = 0; i<100000000; i++){
            dos.writeInt(i);
        }
        dos.flush();
    }

    @Test
    public void readBytes() throws IOException {
        int val;
        file1.seek(0);
        int fileLength = (int)file1.length();
        byte[] bytes = new byte[4];
        for (int i=0; i<fileLength/4; i++){
            dis.read(bytes, 0, 4);
            val = ExternalSort.byteArrayToInt(bytes);
        }
    }

    @Test
    public void readInts() throws IOException {
        int val;
        file1.seek(0);
        int fileLength = (int)file1.length();
        for (int i = 0; i<fileLength/4; i++){
            val = dis.readInt();
        }
    }

}
