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

    @SuppressWarnings("resource")
    public static int preSort(String f1, String f2) throws IOException {
        int blockSizeAlreadySorted;

        RandomAccessFile a1 = new RandomAccessFile(f1,"rw");
        RandomAccessFile a2 = new RandomAccessFile(f1,"rw");

        DataOutputStream dosA2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD())));

        // Memory reading in java seems to be extremely buggy
        double memory = Runtime.getRuntime().freeMemory();

        int byteNum;
        int intNum;
        int[] ints;

        if (a1.length() <= memory/6) { // divide by something to be safe
            System.out.println("File can fit into main memory - sorting in one go");
            byteNum = (int)(a1.length());
            intNum = byteNum/4;

            ints = extractToArray(a1, 0, intNum);
            innerSort(ints);

            for (int datum : ints){
                dosA2.writeInt(datum);
            }

            dosA2.flush();
            blockSizeAlreadySorted = intNum;

        } else {
            double a1Length = a1.length();
            System.out.println("File is too big to sort in one go, it's " + a1Length + " bytes long!");
            memory = Runtime.getRuntime().freeMemory();
            byteNum = (int) (memory/8);
            intNum = byteNum/4;

            for (int i = 0; i <= (int)a1Length/4; i += intNum){

                ints = extractToArray(a1, i, intNum);
                innerSort(ints);

                for (int datum: ints){
                    dosA2.writeInt(datum);
                }

            }
            dosA2.flush();
            blockSizeAlreadySorted = intNum;

        }

        ints = null;

        return blockSizeAlreadySorted;
    }

    @SuppressWarnings("resource")
    private static int[] extractToArray(RandomAccessFile inputFile, int startInt, int numberOfInts) throws IOException {
        // Set up a stream for the input file
        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));

        // Set up the list
        int[] array = new int[numberOfInts];

        // Skip the input stream to the correct point
        inputStream.skipBytes(4*startInt);

        int read = 0;
        // Add the required integers
        for (int i = 0; i < numberOfInts; i++){
            try{
                array[i] = inputStream.readInt();
                read += 1;
            } catch (EOFException e) {
                break;
            }
        }

        // Reset the file pointer
        inputFile.seek(0);

        int[] correctSizeArray = Arrays.copyOf(array, read);

        return correctSizeArray;
    }

    private static void innerSort(int[] ints) {
        // In-place quick sort
        Arrays.sort(ints);
    }

    @SuppressWarnings("resource")
    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        // Set up RandomAccessFiles for each file
        RandomAccessFile fileA = new RandomAccessFile(f1, "rw");
        RandomAccessFile fileA2 = new RandomAccessFile(f1, "rw");
        RandomAccessFile fileB = new RandomAccessFile(f2, "rw");
        RandomAccessFile fileB2 = new RandomAccessFile(f2, "rw");

        long fileLength = fileA.length();

        // Presort it
        int alreadySorted = preSort(f1, f2);

        if (alreadySorted >= fileLength/4){
            return;
        }

        int numberOfIntsInFile = (int) fileA.length()/4;
        int blockSize = alreadySorted*2;

        RandomAccessFile activeInput = fileA;
        RandomAccessFile activeInput2 = fileA2;
        RandomAccessFile activeOutput = fileB;

        while (blockSize/2 <= numberOfIntsInFile){

            for (int i = 0; i < numberOfIntsInFile; i += blockSize){
                mergeToFile(activeInput, activeInput2, activeOutput, blockSize, i);
            }

            blockSize *= 2;
            fileA.seek(0);
            fileA2.seek(0);
            fileB.seek(0);

            if (activeInput == fileA) {
                activeInput = fileB;
                activeInput2 = fileB2;
                activeOutput = fileA;
            } else {
                activeInput = fileA;
                activeInput2 = fileA2;
                activeOutput = fileB;
            }
        }

        // Now, if finished in B, copy across to A (accounting for final switch of activeOutput)
        if (activeOutput == fileA) {
            copyFile(fileB, fileA);
        }

    }

    @SuppressWarnings("resource")
    private static void copyFile(RandomAccessFile inputFile, RandomAccessFile outputFile) throws IOException {
        // Copy contents of inputFile to outputFile
        inputFile.seek(0);
        outputFile.seek(0);

        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile.getFD())));

        while (in.available() > 3){
            out.writeInt(in.readInt());
        }

        out.flush();

    }

    @SuppressWarnings("resource")
    public static void mergeToFile(RandomAccessFile inputFile, RandomAccessFile inputFile2, RandomAccessFile outputFile, int numberOfIntsInBlock, int startBlock) throws IOException {
        inputFile.seek(0);
        inputFile2.seek(0);

        int intsPerStream1 = (numberOfIntsInBlock/2);
        int intsPerStream2 = intsPerStream1;


        // Set up two streams for the input file
        DataInputStream inputStream1 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));
        DataInputStream inputStream2 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile2.getFD())));

        inputStream1.skipBytes(4*startBlock);
        inputStream2.skipBytes(4*startBlock + intsPerStream1*4);

        // Set up an outputStream
        DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile.getFD())));

        Integer val1 = null;
        Integer val2 = null;
        int intsWrittenFrom1 = 0;
        int intsWrittenFrom2 = 0;

        while (intsWrittenFrom1 < intsPerStream1 || intsWrittenFrom2 < intsPerStream2){
            if (val1 == null){
                try {
                    val1 = inputStream1.readInt();
                } catch (EOFException e) {
                    val1 = null;
                }

            }

            if (val2 == null) {
                try{
                    val2 = inputStream2.readInt();
                } catch (EOFException e){
                    val2 = null;
                }
            }
            if (intsWrittenFrom1 < intsPerStream1 && intsWrittenFrom2 < intsPerStream2 && val1 != null && val2 != null){
                // Both should have integers left if block is full
                if (val1 <= val2){
                    outputStream.writeInt(val1);
                    intsWrittenFrom1 += 1;
                    val1 = null;
                } else {
                    outputStream.writeInt(val2);
                    intsWrittenFrom2 += 1;
                    val2 = null;
                }
            } else if (intsWrittenFrom1 < intsPerStream1 && val1 != null) {
                // Only list1 has integers
                outputStream.writeInt(val1);
                intsWrittenFrom1 += 1;
                val1 = null;
            } else if (intsWrittenFrom2 < intsPerStream2 && val2 != null) {
                // Only list2 has integers
                outputStream.writeInt(val2);
                intsWrittenFrom2 += 1;
                val2 = null;
            } else {
                break;
            }
        }
        outputStream.flush();
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
        System.out.println("The checksum is: "+checkSum(f1));
    }

}
