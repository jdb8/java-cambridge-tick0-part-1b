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

    public StreamBlock(int startIntOfBlock, int numOfIntsInBlock, String fileName) throws IOException, EOFException{
        file = new RandomAccessFile(fileName, "r");
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getFD())));
        numberOfInts = numOfIntsInBlock;

        data.skipBytes(startIntOfBlock*4);

        advance();
        //System.out.println("Instantiated new StreamBlock");

    }

    private int popIntFromByteArray(){
        byte[] temp = {byteArray[0], byteArray[1], byteArray[2], byteArray[3]};
        int val = ExternalSort.byteArrayToInt(temp);

        int newLength = byteArray.length - 4;
        if (newLength == 0){
            this.byteArray = null;
        } else {
            byte[] newArray = new byte[newLength];

            int length = newArray.length;
            for (int i = 0; i < length; i++){
                newArray[i] = byteArray[4+i];
            }

            this.byteArray = newArray;
        }

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
            if (byteArray == null || byteArray.length == 0){
                byteArray = new byte[20];
                int test = data.read(byteArray, 0, 20);
                if (test == -1){
                    throw new EOFException();
                }
            }

            head = popIntFromByteArray();
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
