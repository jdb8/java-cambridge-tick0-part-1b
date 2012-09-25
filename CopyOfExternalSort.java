package uk.ac.cam.jdb75.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CopyOfExternalSort {

    public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
        RandomAccessFile a1 = new RandomAccessFile(f1,"rw");
        RandomAccessFile a2 = new RandomAccessFile(f1,"rw");
        RandomAccessFile b1 = new RandomAccessFile(f2,"rw");

//        DataInputStream disA1 = new DataInputStream(new BufferedInputStream(new FileInputStream(a1.getFD())));
//        DataInputStream disA2 = new DataInputStream(new BufferedInputStream(new FileInputStream(a2.getFD())));
//        DataInputStream disB1 = new DataInputStream(new BufferedInputStream(new FileInputStream(b1.getFD())));
//
//        DataOutputStream dosA1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b1.getFD())));
//        DataOutputStream dosB1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b1.getFD())));

        int numberOfIntegers = (int) a1.length()/4;

        int sizeOfBlock = 1;
        RandomAccessFile activeInputFile = a1;
        DataOutputStream activeOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b1.getFD())));
        boolean inputIsA = true;

        while (sizeOfBlock < numberOfIntegers) {
            System.out.println("INFO: MERGING TWO BLOCKS OF SIZE " + sizeOfBlock + " - input is A? " + inputIsA);
            mergeToFile(activeOutputStream, activeInputFile, activeInputFile, sizeOfBlock, 0);


            if (inputIsA) {
                activeInputFile = b1;
                activeOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a1.getFD())));
            } else {
                activeInputFile = a1;
                activeOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b1.getFD())));
            }
            sizeOfBlock *= 2;
            inputIsA = !inputIsA;
        }

//        disA2.skipBytes(4);
//
//        int int1;
//        int int2;
//        while(disA1.available() > 3 && disA2.available() > 3){
//            int1 = disA1.readInt();
//            int2 = disA2.readInt();
//
//            if (int1 <= int2){
//                dosB1.writeInt(int1);
//                dosB1.writeInt(int2);
//            } else {
//                dosB1.writeInt(int2);
//                dosB1.writeInt(int1);
//            }
//
//        }
//        dosB1.flush();
    }

    public static void mergeToFile(DataOutputStream outputStream, RandomAccessFile inputFile, RandomAccessFile inputFile2, int sizeOfBlocks, int startBlock) throws IOException{
        DataInputStream inputStream1 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));
        DataInputStream inputStream2 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile2.getFD())));

        List<Integer> block1 = blockToIntList(inputFile, sizeOfBlocks, startBlock);
        List<Integer> block2 = blockToIntList(inputFile2, sizeOfBlocks, startBlock+sizeOfBlocks);

        for (int integer : block1){
            System.out.print("block1 = ");
            System.out.print(integer);

        }
        System.out.println("");

        for (int integer : block2){
            System.out.print("block2 = ");
            System.out.print(integer);

        }
        System.out.println("");


        Integer val1 = null;
        Integer val2 = null;

        for (int i = 0; i < sizeOfBlocks*2; i++){

            if (block1.size() > 0){

                if (val1 == null) {
                    val1 = block1.get(0);
                }

                if (block2.size() > 0) {

                    if (val2 == null) {
                        val2 = block2.get(0);
                    }

                    System.out.println("val1 = " + val1);
                    System.out.println("val2 = " + val2);

                    // both streams have ints left
                    if (val1 <= val2) {
                        outputStream.writeInt(val1);
                        System.out.println("WROTE val1, " + val1 + " because val1 was <= val2");
                        block1.remove(0);
                        val1 = null;
                    } else {
                        outputStream.writeInt(val2);
                        System.out.println("WROTE val2, " + val2 + " because val2 < val1");
                        block2.remove(0);
                        val2 = null;
                    }
                } else {
                    // only stream 1 has ints left
                    outputStream.writeInt(val1);
                    System.out.println("WROTE val1, " + val1 + " because only stream 1 had ints left");
                    block2.remove(0);
                    val1 = null;
                }
            } else {
                if (block2.size() > 0) {

                    if (val2 == null) {
                        val2 = block2.get(0);
                    }

                    // only stream 2 has ints left
                    outputStream.writeInt(val2);
                    System.out.println("WROTE val2, " + val2 + " because only stream 2 had ints left");
                    block2.remove(0);
                    val2 = null;
                } else {
                    // neither stream has ints left
                    System.out.println("Neither block has integers left");
                    break;
                }
            }
        }

    }

    public static void mergeToFile3(DataOutputStream outputStream, RandomAccessFile inputFile, RandomAccessFile inputFile2, int sizeOfBlocks, int startBlock) throws IOException{
        DataInputStream inputStream1 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));
        DataInputStream inputStream2 = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile2.getFD())));

        inputStream1.skipBytes(4*startBlock);
        inputStream2.skipBytes(4*startBlock);

        Integer val1 = null;
        Integer val2 = null;
        int inputStream1Count = 0;
        int inputStream2Count = 0;
        // find some way of limiting how far each stream can walk

        inputStream2.skipBytes(4*sizeOfBlocks);
        System.out.println("Size of blocks is " + sizeOfBlocks + " therefore skipping " + 4*sizeOfBlocks + " bytes");

        for (int i = 0; i < sizeOfBlocks*2; i++){

            if (inputStream1.available() > 3 && inputStream1Count < sizeOfBlocks){

                if (val1 == null) {
                    val1 = inputStream1.readInt();

                }

                if (inputStream2.available() > 3 && inputStream2Count < sizeOfBlocks) {

                    if (val2 == null) {
                        val2 = inputStream2.readInt();
                        inputStream2Count += 1;
                    }

                    System.out.println("val1 = " + val1);
                    System.out.println("val2 = " + val2);

                    // both streams have ints left
                    if (val1 <= val2) {
                        outputStream.writeInt(val1);
                        inputStream1Count += 1;
                        System.out.println("WROTE val1, " + val1 + " because val1 was <= val2");
                        val1 = null;
                    } else {
                        outputStream.writeInt(val2);
                        inputStream2Count += 1;
                        System.out.println("WROTE val2, " + val2 + " because val2 < val1");
                        val2 = null;
                    }
                } else {
                    // only stream 1 has ints left
                    outputStream.writeInt(val1);
                    inputStream1Count += 1;
                    System.out.println("WROTE val1, " + val1 + " because only stream 1 had ints left");
                    val1 = null;
                }
            } else {
                if (inputStream2.available() > 3 && inputStream2Count < sizeOfBlocks) {

                    if (val2 == null) {
                        val2 = inputStream2.readInt();
                        inputStream2Count += 1;
                    }

                    // only stream 2 has ints left
                    outputStream.writeInt(val2);
                    inputStream2Count += 1;
                    System.out.println("WROTE val2, " + val2 + " because only stream 2 had ints left");
                    val2 = null;
                } else {
                    // neither stream has ints left
                    System.out.println("Neither stream has 4 bytes left");
                    break;
                }
            }
        }
        outputStream.flush();
        inputFile.seek(0);
        inputFile2.seek(0);
    }

    public static void mergeToFile2(DataOutputStream outputStream, List<Integer> block1, List<Integer> block2, int sizeOfBlocks) throws IOException{
        Integer val1;
        Integer val2;
        if (block1.size() != sizeOfBlocks || block2.size() != sizeOfBlocks){
            System.out.println("ERROR: blocks passed to mergeToFile were not the correct size");
            System.out.println("ERROR: expected blocks of size " + sizeOfBlocks + " but were actually " + block1.size() + " and " + block2.size());
            return;
        }

        for (int i = 0; i < sizeOfBlocks*2; i++){
            if (block1.size() > 0){
                val1 = block1.get(0);
            } else {
                val1 = null;
            }

            if (block2.size() > 0){
                val2 = block2.get(0);
            } else {
                val2 = null;
            }

            System.out.println("MERGETOFILE: val1 = " + val1);
            System.out.println("MERGETOFILE: val2 = " + val2);

            if (val1 == null && val2 == null) {
                break;
            } else if (val2 == null) {
                if (val1 != null){
                    outputStream.writeInt(val1);
                    block1.remove(0);
                }
            } else {
                outputStream.writeInt(val2);
                block2.remove(0);
            }
        }
        outputStream.flush();
    }

	public static void sort2(String f1, String f2) throws FileNotFoundException, IOException {

	    System.out.println("--------------------------");
	    System.out.println(f1 + ", " + f2);
	    System.out.println("--------------------------");

	    RandomAccessFile a1 = new RandomAccessFile(f1,"rw");
	    RandomAccessFile a2 = new RandomAccessFile(f1,"rw");
	    RandomAccessFile b1 = new RandomAccessFile(f2,"rw");
        //RandomAccessFile b2 = new RandomAccessFile(f2,"r");

	    DataOutputStream dosA2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD())));
        DataInputStream disA1 = new DataInputStream(new BufferedInputStream(new FileInputStream(a1.getFD())));

	    double memory = Runtime.getRuntime().freeMemory();

	    int byteNum;
	    int intNum;
	    List<Integer> ints;
	    int size = 0;
	    if (a1.length() <= memory/4) { // divide by something to be safe
	        memory = Runtime.getRuntime().freeMemory();
            System.out.println("Can do all at once: file has " + a1.length() + " bytes and memory/4 is " + memory/4);
            byteNum = (int)(a1.length());
            intNum = byteNum/4;
            ints = blockToIntList(a1, byteNum, 0);
            innerSort(ints);
            System.out.println("Writing to file A");
            for (int datum : ints){
                dosA2.writeInt(datum);
            }
            dosA2.flush();

        } else {
            DataOutputStream dosB1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b1.getFD())));
            double a1Length = a1.length();
            System.out.println("File contains " + a1Length + " bytes");
//            double ratio = a1Length/memory;
//            byteNum = (int) (a1Length/Math.ceil(ratio));
            //int blockSize = 800000; // arbitrary
            memory = Runtime.getRuntime().freeMemory();
            int blockSize = (int) (memory);
            System.out.println("Blocksize is " + blockSize);
            //intNum = byteNum/4;
            int writing = 0;
            int blockCount = 0;
            for (int i = 0; i < (int)a1Length; i += blockSize){

                ints = blockToIntList(a1, blockSize, 0);
                innerSort(ints);

                //System.out.println("Sorted block of " + ints.size());

                for (int datum: ints){
                    dosB1.writeInt(datum);
                    writing += 1;
                }

                //System.out.println("Wrote " + writing + " integers to B, settings ints to null");
                blockCount += 1;
                ints = null;

            }
            System.out.println("There are " + blockCount + " blocks in file B now");
            dosB1.flush();

        }

	    System.out.println("The file is " + a1.length() + " bytes long");
	    System.out.println("therefore the file has " + a1.length()/4 + " integers");

	    ints = null;
	    //a1iS.close();
        //a1.close();
        //b1.close();


	}

//	private static int[] blockToIntArray(RandomAccessFile input, int firstByte, int numberOfBytes) throws IOException, EOFException{
//	    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input.getFD())));
//	    int intNum = numberOfBytes/4;
//	    int[] ints;
//
//	    ints = new int[intNum];
//
//	    inputStream.skipBytes(firstByte);
//        for (int i = 0; i < intNum; i++){
//            try {
//                ints[i] = inputStream.readInt();
//            } catch (EOFException e) {
//                //System.out.println("EOFException thrown");
//                //throw new EOFException();
//                break;
//            }
//
//        }
//
//	    return ints;
//	}

	private static List<Integer> blockToIntList(RandomAccessFile input, int numberOfBytes, int startByte) throws IOException {
	    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input.getFD())));

	    inputStream.skipBytes(startByte);

	    List<Integer> ints = new ArrayList<Integer>();

	    int intNum = numberOfBytes/4;
        for (int i = 0; i < intNum; i++){
            try {
                int val = inputStream.readInt();
                ints.add(val);
            } catch (EOFException e) {
                //System.out.println("EOFException thrown");
                //throw new EOFException();
                break;
            }

        }

        input.seek(0);
        return ints;
	}

	public static int countTotalIntsInFile(RandomAccessFile input) throws IOException {
	    DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(input.getFD())));
	    int size = 0;

	    // work out a block size:
	    int blockSize = 8000; // arbitrary

	    double memory = Runtime.getRuntime().freeMemory();
	    double memMB = memory/(1024*1024);

	    //int blockSize = (int) (memory/16);

	    List<Integer> ints;

	    for (int i = 0; i<input.length(); i += blockSize){
	        ints = blockToIntList(input, blockSize, 0);
	        size += ints.size();
	        //System.out.println("Made a list, size: " + ints.size() + " - running total: " + size);
	        ints = null;
	    }

	    ints = null;


	    return size;

	}

//	private static void sortBlock(int[] data, RandomAccessFile output) throws IOException{
//	    quickSort(data);
//	    //DataOutputStream dosA = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(input.getFD())));
//	    DataOutputStream dosB = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output.getFD())));
//	    writeIntsToFile(data, dosB);
//	}
//
//	private static void writeIntsToFile(int[] data, DataOutputStream outputStream) throws IOException{
//
//        for (int datum : data){
//            outputStream.writeInt(datum);
//        }
//        output.flush();
//        //dosB.close();
//	}

	private static void copyFile(RandomAccessFile input, RandomAccessFile output) throws IOException{
	    System.out.println("copyFile()");
	    DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(input.getFD())));
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output.getFD())));

//        // Transfer bytes from in to out
//        byte[] buf = new byte[1024];
//        int len;
//        while ((len = in.read(buf)) > 0) {
//            out.write(buf, 0, len);
//        }
//        in.close();
//        out.close();
        try {
            int contents = in.read();
            System.out.println(contents);
            while (contents != -1){
                out.write(contents);
                System.out.println(contents);
                contents = in.read();
            }
        } catch (EOFException e){
            System.out.println("EOF");
        }

	}

//	private static void copyInts(RandomAccessFile input, RandomAccessFile output, int startByte, int blockLength) throws IOException {
//	    DataInputStream dosInput = new DataInputStream(new BufferedInputStream(new FileInputStream(input.getFD())));
//	    DataOutputStream dosOutput = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(output.getFD())));
//
//	    dosInput.skipBytes(startByte);
//
//	    int intNum = blockLength/4;
//	    int val;
//	    int[] ints = new int[intNum];
//
//	    for (int i = 0; i < intNum; i++){
//            if (dosInput.available()>0){
//                val = dosInput.readInt();
//                System.out.println(val);
//                ints[i] = val;
//            } else {
//                break;
//            }
//        }
//
//	    writeIntsToFile(ints, output);
//	    ints = null;
//	    //dosInput.close();
//	    //dosOutput.close();
//
//	}

	private static void innerSort(List<Integer> ints){
	    // In-place quick sort
	    //Arrays.sort(ints);
	    Collections.sort(ints);
	}

	private static String byteToHex(byte b) {
		String r = Integer.toHexString(b);
		if (r.length() == 8) {
			return r.substring(6);
		}
		return r;
	}

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
		sort(f1, f2);
		System.out.println("The checksum is: "+checkSum(f1));
	}
}
