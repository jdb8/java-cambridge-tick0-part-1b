/**
 *
 */
package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.cam.jdb75.fjava.tick0.ExternalSort;

/**
 * @author joe
 *
 */
@RunWith(value = Parameterized.class)
public class ReadFileToIntArrayTest {

    private Integer currentNum;
    private static Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private RandomAccessFile file;
    private DataOutputStream dos;

    public ReadFileToIntArrayTest(Integer num) {
        this.currentNum = num;
    }

    @Parameters
    public static Collection<Object[]> generateData() {
        Object[][] objs = new Object[17][1];
        for (int i = 0; i < 17; i++){
            objs[i][0] = ints[i];
        }
        return Arrays.asList(objs);
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        System.out.println("--------------------------------------------");
        String input = "test-suite/test" + this.currentNum + "a.dat";
        System.out.println("Now testing " + input);
        System.out.println("Current memory is " + Runtime.getRuntime().freeMemory()/(1024*1024) + " mb.");
        System.out.println("--------------------------------------------");
        this.file = new RandomAccessFile(input,"rw");
        //this.dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(this.file.getFD())));
    }

    @Test
    public void test() throws IOException {
        long numberOfBytesInFile = this.file.length();
        int numberOfIntsInFile = (int) (numberOfBytesInFile/4);


        assertEquals(numberOfIntsInFile, ExternalSort.countTotalIntsInFile(this.file));
    }

    @After
    public void tearDown() throws Exception {
        System.gc();
        System.out.println("--------------------------------------------");
    }

}
