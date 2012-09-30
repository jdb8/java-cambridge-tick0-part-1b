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
    int[] vals2 = {5,7,0,1,8,9,2,3,4,6};

    @Before
    public void setUp() throws Exception {
        this.file1 = new RandomAccessFile("example1", "rw");
        this.file2 = new RandomAccessFile("example2", "rw");

        for (int val : vals2){
            file1.writeInt(val);
            file2.writeInt(0);
        }

        file1.seek(0);
        file2.seek(0);

    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        ExternalSort.multipleWayMergeToFile("example1", "example1", 2);

        file1.seek(0);

        Arrays.sort(vals2);

        int temp;
        for (int val : vals2){
            temp = file1.readInt();
            assertEquals(val, temp);
        }

    }

}
