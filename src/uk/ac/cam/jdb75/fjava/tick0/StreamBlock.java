package uk.ac.cam.jdb75.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class StreamBlock {

    private int head;
    private RandomAccessFile file;
    private DataInputStream data;
    private int readCount = 0;
    private int numberOfInts;
    private byte[] byteArray;
    private int[] intArray;
    private byte[] byteTemp = new byte[4];
    private int intPointer = 0;

    public StreamBlock(int startIntOfBlock, int numOfIntsInBlock, String fileName) throws IOException, EOFException{
        file = new RandomAccessFile(fileName, "r");
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getFD())));
        numberOfInts = numOfIntsInBlock;
        
        byteArray = new byte[numberOfInts/4];

        data.skipBytes(startIntOfBlock*4);

        advance();
        //System.out.println("Instantiated new StreamBlock");

    }

    private int popIntFromBuffer(){
        int val = intArray[intPointer];
        intPointer++;
        return val;
    }

    public int getBlockSize(){
        return this.numberOfInts;
    }

    public void advance() throws IOException, EOFException{
        if (readCount >= numberOfInts){
            file.close();
            data.close();
            throw new EOFException();
        } else {
            if (intArray == null || intPointer >= intArray.length){
                int test = data.read(byteArray);
                if (test == -1){
                    throw new EOFException();
                }
                intPointer = 0;
                intArray = ExternalSort.byteArrayToIntArray(byteArray);
            }

            head = popIntFromBuffer();
            readCount++;

        }

    }

    public int getHead(){
        return this.head;
    }

    public int getReadCount(){
        return this.readCount;
    }

    public int pop() throws EOFException, IOException, LastIntException{
        int val = getHead();

        if (readCount == numberOfInts){
            throw new LastIntException(val);
        } else {
            advance();
            return val;
        }

    }


}
