package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;

public class TestJavaStreams {

    RandomAccessFile file1;
    RandomAccessFile file2;
    DataInputStream[] iss;
    int[] vals = {1,2,3,4,5,6,7,8,9};

    @Before
    public void setUp() throws Exception {
        this.file1 = new RandomAccessFile("example1", "rw");
        this.file2 = new RandomAccessFile("example1", "r");
        iss = new DataInputStream[9];

        RandomAccessFile newFile;
        for (int i = 0; i<9; i++){
            file1.seek(0);
            newFile = new RandomAccessFile("example1", "r");
            iss[i] = new DataInputStream(new BufferedInputStream(new FileInputStream(newFile.getFD())));
            iss[i].skipBytes(4*i);
        }

        for (int val : vals){
            file1.writeInt(val);
        }

        file1.seek(0);
        file2.seek(0);

    }

    class MyFileInputStream extends FileInputStream {
        public MyFileInputStream(FileDescriptor fd) {super(fd);}
        protected void finalize() {} //disable double-free of file descriptor
       }

    @Test
    public void test() throws FileNotFoundException, IOException {
        int val;
        for (int i = 0; i<iss.length; i++){
            val = iss[i].readInt();
            System.out.println(val);
            assertEquals(vals[i], val);
        }


    }

}
