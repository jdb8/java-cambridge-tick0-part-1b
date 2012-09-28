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
    private int startIntOfBlock;
    private int readCount = 0;
    private int numberOfInts;
    private byte[] byteArray;
    private boolean canWrite = true;

    public StreamBlock(int startIntOfBlock, int numOfIntsInBlock, String fileName) throws IOException, EOFException{
        file = new RandomAccessFile(fileName, "r");
        this.startIntOfBlock = startIntOfBlock;
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(file.getFD())));
        numberOfInts = numOfIntsInBlock;

        data.skipBytes(startIntOfBlock*4);

        advance();
        System.out.println("Instantiated new StreamBlock");

    }

    public int getBlockSize(){
        return this.numberOfInts;
    }

    public void advance() throws IOException, EOFException {
        if (readCount >= numberOfInts){
            file.close();
            data.close();
            throw new EOFException("End of block, read " + readCount + " ints total");
        } else {
            //file.seek(0);

            head = data.readInt();
            readCount += 1;
            //System.out.println("StreamBlock: set head to " + getHead());
        }

    }

    public int getHead(){
        return this.head;
    }

    public int getReadCount(){
        return this.readCount;
    }

    public int pop() throws EOFException, IOException{
        int val = getHead();
        advance();
        return val;
    }


}
