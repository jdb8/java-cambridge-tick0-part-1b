package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;
import uk.ac.cam.jdb75.fjava.tick0.ThreadedSort;

public class TestThreadedSort {

    RandomAccessFile file1;
    RandomAccessFile file2;

    @Before
    public void setUp() throws Exception {
        this.file1 = new RandomAccessFile("example1", "rw");
        this.file2 = new RandomAccessFile("example2", "rw");

        file1.writeInt(3);
//        file1.writeInt(4);
       file1.writeInt(1);
//        file1.writeInt(2);
//        file1.writeInt(7);
//        file1.writeInt(5);
//        file1.writeInt(9);
//        file1.writeInt(6);
//        file1.writeInt(10);
//        file1.writeInt(3);
//        file1.writeInt(4);
//        file1.writeInt(1);
//        file1.writeInt(2);
//        file1.writeInt(7);
//        file1.writeInt(5);
//        file1.writeInt(9);
//        file1.writeInt(6);
        //file1.setLength(17*4);

//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
//        file2.writeInt(0);
        file2.writeInt(0);
        file2.writeInt(0);
        //file2.setLength(17*4);

        file1.seek(0);
        file2.seek(0);

    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        ThreadedSort.sort("example1", "example2");

        file1.seek(0);
        int val = file1.readInt();
        assertEquals(1, val);
//        val = file1.readInt();
//        assertEquals(1, val);
//        val = file1.readInt();
//        assertEquals(2, val);
//        val = file1.readInt();
//        assertEquals(2, val);
        val = file1.readInt();
        assertEquals(3, val);
        //val = file1.readInt();
//        assertEquals(3, val);
//        val = file1.readInt();
//        assertEquals(4, val);
//        val = file1.readInt();
//        assertEquals(4, val);
//        val = file1.readInt();
//        assertEquals(5, val);
//        val = file1.readInt();
//        assertEquals(5, val);
//        val = file1.readInt();
//        assertEquals(6, val);
//        val = file1.readInt();
//        assertEquals(6, val);
//        val = file1.readInt();
//        assertEquals(7, val);
//        val = file1.readInt();
//        assertEquals(7, val);
//        val = file1.readInt();
//        assertEquals(9, val);
//        val = file1.readInt();
//        assertEquals(9, val);
//        val = file1.readInt();
//        assertEquals(10, val);
    }

}
