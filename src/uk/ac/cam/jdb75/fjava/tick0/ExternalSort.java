package uk.ac.cam.jdb75.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ExternalSort {


    /* from http://stackoverflow.com/a/5399829 */
    protected static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            value = (value << 8) | (b[i] & 0xFF);
        }
        return value;
    }
    private static byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    @SuppressWarnings("resource")
    private static int preSort(String f1, String f2, int length) throws IOException {
        int blockSizeAlreadySorted;

        RandomAccessFile a1 = new RandomAccessFile(f1,"r");
        RandomAccessFile b = new RandomAccessFile(f2, "rw");

        DataOutputStream dosB = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD())));

        int byteNum;
        int intNum;
        int[] ints;

        if (length <= 800000) { // divide by something to be safe
            //System.out.println("File can fit into main memory - sorting in one go");
            byteNum = (int)(a1.length());
            intNum = byteNum/4;

            ints = extractToArray(a1, 0, intNum);
            innerSort(ints);

            dosB.write(intArrayToByteArray(ints));

            dosB.flush();
            blockSizeAlreadySorted = intNum;

        } else {
            //System.out.println("File is too big to sort in one go, it's " + a1Length + " bytes long!");
            byteNum = 800000;
            intNum = byteNum/4;

            for (int i = 0; i <= (int)length/4; i += intNum){

                ints = extractToArray(a1, i, intNum);
                innerSort(ints);

                dosB.write(intArrayToByteArray(ints));

            }
            dosB.flush();
            blockSizeAlreadySorted = intNum;

        }

        return blockSizeAlreadySorted;
    }

    protected static int[] byteArrayToIntArray(byte[] array){
        int[] intArray = new int[array.length/4];
        int intIndex = 0;
        byte[] temp = new byte[4];
        for (int i = 0; i < array.length; i += 4){
            temp[0] = array[i];
            temp[1] = array[i+1];
            temp[2] = array[i+2];
            temp[3] = array[i+3];
            intArray[intIndex] = byteArrayToInt(temp);
            intIndex++;
        }
        return intArray;
    }

    private static byte[] intArrayToByteArray(int[] array){
        byte[] byteArray = new byte[array.length*4];
        byte[] temp;
        int byteIndex = 0;
        for (int i = 0; i < array.length; i++){
            temp = intToByteArray(array[i]);
            byteArray[byteIndex] = temp[0];
            byteArray[byteIndex+1] = temp[1];
            byteArray[byteIndex+2] = temp[2];
            byteArray[byteIndex+3] = temp[3];
            byteIndex += 4;
        }
        return byteArray;
    }

    @SuppressWarnings("resource")
    private static int[] extractToArray(RandomAccessFile inputFile, int startInt, int numberOfInts) throws IOException {

        // Set up a stream for the input file
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));

        //byte[][] array = new byte[numberOfInts][4];
        byte[] all = new byte[numberOfInts*4];

        // Skip the input stream to the correct point
        inputStream.skipBytes(4*startInt);

        int read = 0;
        try{
            read = inputStream.read((all), 0, numberOfInts*4);
        } catch (EOFException e) {
        }


        int[] intArray = byteArrayToIntArray(all);


        // Reset the file pointer
        inputFile.seek(0);

        int[] correctSizeArray = Arrays.copyOf(intArray, read/4);

        return correctSizeArray;
    }

    private static void innerSort(int[] array) {
        // quick sort
        Arrays.sort(array);
    }

    @SuppressWarnings("resource")
    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        // Set up RandomAccessFiles for each file
        RandomAccessFile fileA = new RandomAccessFile(f1, "r");

        long fileLength = fileA.length();

        // arbitrary
        int memory = 800000;

        if (fileLength <= memory){
            preSort(f1, f1, (int)fileLength);
            return;
        } else {
            int alreadySorted = preSort(f1, f2, (int)fileLength);
            multipleWayMergeToFile(f2, f1, alreadySorted/2);
        }

    }

    @SuppressWarnings("resource")
    private static void multipleWayMergeToFile(String inputFileName, String outputFileName, int numberOfIntsInBlock) throws IOException{
        RandomAccessFile inputFile = new RandomAccessFile(inputFileName, "r");
        RandomAccessFile outputFile = new RandomAccessFile(outputFileName, "rw");
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile.getFD())));

        int totalIntsInFile = (int) inputFile.length()/4;
        int numberOfBlocks = (totalIntsInFile/numberOfIntsInBlock);

        StreamMinHeap heap = new StreamMinHeap(numberOfBlocks+1);
        //StreamBlock[] inputStreams = new StreamBlock[numberOfBlocks];

        //System.out.println("Number of ints in file = " + totalIntsInFile + ", numberOfBlocks = " + numberOfBlocks + ", intsInBlock = " + numberOfIntsInBlock);

        for (int i = 0; i <= numberOfBlocks; i++){
            try {
                StreamBlock newStreamBlock = new StreamBlock(i*numberOfIntsInBlock, numberOfIntsInBlock, inputFileName);
                heap.insert(newStreamBlock);
            } catch (EOFException e){
                //System.out.println("EOF IN STREAM BLOCK CONSTRUCTOR");
            }

            //System.out.println("Inserted a streamblock");
        }

        //System.out.println("heap size: " + heap.size());

        StreamBlock streamSmallest = null;
        int valSmallest = 0;
        int buffer = 40000;
        byte[] byteArray = new byte[buffer];
        byte[] temp;
        int bytePosition = 0;
        for (int i = 0; i < totalIntsInFile; i++){
            try{
                try{
                    streamSmallest = heap.removeMin();
                    valSmallest = streamSmallest.pop();
                    heap.insert(streamSmallest);
                } catch (LastIntException e){
                    valSmallest = e.lastInt;
                    //System.out.println("Last int is " + valSmallest);
                } catch (EOFException e){
                    // block ended prematurely, get last integer
                    valSmallest = streamSmallest.getHead();
                } finally {
                    if (i % (buffer/4) == 0 && i != 0){
                        outputStream.write(byteArray);
                        byteArray = new byte[buffer];
                        bytePosition = 0;
                    }
                    temp = intToByteArray(valSmallest);
                    byteArray[bytePosition*4] = temp[0];
                    byteArray[bytePosition*4 + 1] = temp[1];
                    byteArray[bytePosition*4 + 2] = temp[2];
                    byteArray[bytePosition*4 + 3] = temp[3];
                    bytePosition++;
                    //System.out.println("Wrote " + valSmallest);
                }
            } catch(RuntimeException e){
                //System.out.println("had to break: " + e.getMessage());
            } catch(EOFException e){
                //System.out.println("Non-full block");
            }
        }
        outputStream.write(byteArray);
        outputStream.flush();
        //System.out.println("At end: Number of ints written = " + written + ", number in file = " + totalIntsInFile + ", blockSize = " + numberOfIntsInBlock);
        //System.out.println("Final heap: " + heap);

    }

    private static String byteToHex(byte b) {
        String r = Integer.toHexString(b);
        if (r.length() == 8) {
            return r.substring(6);
        }
        return r;
    }

    @SuppressWarnings("resource")
    public static String checkSum(String f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream ds = new DigestInputStream(
                    new FileInputStream(f), md);
            byte[] b = new byte[512];
            while (ds.read(b) != -1)
                ;

            String computed = "";
            for(byte v : md.digest())
                computed += byteToHex(v);

            return computed;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<error computing checksum>";
    }

    public static void main(String[] args) throws Exception {
        String f1 = args[0];
        String f2 = args[1];
        //long start = System.currentTimeMillis();
        sort(f1, f2);
        //long end = System.currentTimeMillis();
        //System.out.println("Execution time was "+(end-start)/1000+"s.");
        //System.out.println("The checksum is: "+checkSum(f1));
    }

}
