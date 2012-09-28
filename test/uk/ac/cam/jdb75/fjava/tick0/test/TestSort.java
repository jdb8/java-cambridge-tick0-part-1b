package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;

public class TestSort {

    RandomAccessFile file1;
    RandomAccessFile file2;
    int[] vals = {2,4,1,5,7,9,4,8,0,3,10,20,12,14,44,57,2,7,3};
    //int[] vals = {5,7,1,0,9,8,2,3};

    @Before
    public void setUp() throws Exception {
        this.file1 = new RandomAccessFile("example1", "rw");
        this.file2 = new RandomAccessFile("example2", "rw");

        for (int val : vals){
            file1.writeInt(val);
            file2.writeInt(0);
        }

        file1.seek(0);
        file2.seek(0);

    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        ExternalSort.sort("example1", "example2");

        file1.seek(0);

        Arrays.sort(vals);

        int temp;
        for (int val : vals){
            temp = file1.readInt();
            assertEquals(val, temp);
        }

    }

}
