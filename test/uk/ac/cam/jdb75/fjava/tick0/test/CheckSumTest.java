package uk.ac.cam.jdb75.fjava.tick0.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.cam.jdb75.fjava.tick0.*;

@RunWith(value = Parameterized.class)
public class CheckSumTest {

    private String currentCheckSum;
    private Integer currentNum;
    private static String[] checkSums = {"d41d8cd98f0b24e980998ecf8427e", "a54f041a9e15b5f25c463f1db7449", "c2cb56f4c5bf656faca0986e7eba38", "c1fa1f22fa36d331be4027e683baad6", "8d79cbc9a4ecdde112fc91ba625b13c2", "1e52ef3b2acef1f831f728dc2d16174d", "6b15b255d36ae9c85ccd3475ec11c3", "1484c15a27e48931297fb6682ff625", "ad4f60f065174cf4f8b15cbb1b17a1bd", "32446e5dd58ed5a5d7df2522f0240", "435fe88036417d686ad8772c86622ab", "c4dacdbc3c2e8ddbb94aac3115e25aa2", "3d5293e89244d513abdf94be643c630", "468c1c2b4c1b74ddd44ce2ce775fb35c", "79d830e4c0efa93801b5d89437f9f3e", "c7477d400c36fca5414e0674863ba91", "cc80f01b7d2d26042f3286bdeff0d9"};
    private static Integer[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};

    public CheckSumTest(String checkSum, Integer num) {
        this.currentCheckSum = checkSum;
        this.currentNum = num;
    }

    @Parameters
    public static Collection<Object[]> generateData() {
        Object[][] objs = new Object[17][2];
        for (int i = 0; i < 17; i++){
            objs[i][1] = ints[i];
            objs[i][0] = checkSums[i];
        }
//        Object[][] objs = new Object[1][2];
//        objs[0][1] = ints[9];
//        objs[0][0] = checkSums[9];
        return Arrays.asList(objs);
    }

    @Test
    public void sorter() throws FileNotFoundException, IOException {
        String f1 = "test-suite/test"+ this.currentNum + "a.dat";
        String f2 = "test-suite/test"+ this.currentNum + "b.dat";
        //System.out.println("TESTING " + f1 + " and " + f2 + " BELOW");
        long start = System.currentTimeMillis();
        ExternalSort.sort(f1, f2);
        long end = System.currentTimeMillis();

        //System.out.println("Execution time was "+(end-start)+" ms.");
        String newCheckSum = ExternalSort.checkSum(f1);
        assertEquals(this.currentCheckSum, newCheckSum);
    }

    @Before
    public void before() {
        System.gc();
        //double memory = (Runtime.getRuntime().freeMemory()/(1024.0*1024.0));
        //System.out.println(memory);

    }

}
