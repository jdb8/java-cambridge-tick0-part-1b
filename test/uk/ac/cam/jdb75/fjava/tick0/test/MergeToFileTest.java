package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;

public class MergeToFileTest {

    Integer currentNum;
    RandomAccessFile file1;
    RandomAccessFile file1Again;
    RandomAccessFile file2;
    RandomAccessFile file2Again;
    DataOutputStream outputStream2;
    DataOutputStream outputStream1;

    @Before
    public void before() throws IOException {
        this.file1 = new RandomAccessFile("example1", "rw");
        this.file1Again = new RandomAccessFile("example1", "rw");
        this.file2 = new RandomAccessFile("example2", "rw");
        this.file2Again = new RandomAccessFile("example2", "rw");
        this.outputStream2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.file2.getFD())));
        this.outputStream1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.file1.getFD())));

        file1.writeInt(3);
        file1.writeInt(4);
        file1.writeInt(1);

        file2.writeInt(0);
        file2.writeInt(0);
        file2.writeInt(0);

        file1.seek(0);
        file2.seek(0);


    }

    @Test
    public void test() throws IOException, EOFException {
        ExternalSort.mergeToFile(file1, file1Again, file2, 3, 0);

        file2.seek(0);
        int val = file2.readInt();
        assertEquals(1, val);
        val = file2.readInt();
        assertEquals(3, val);
        val = file2.readInt();
        assertEquals(4, val);
    }

}
