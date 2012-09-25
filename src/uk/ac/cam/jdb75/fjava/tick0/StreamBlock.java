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

    public StreamBlock(int startIntOfBlock, int numOfIntsInBlock, RandomAccessFile inputFile) throws IOException{
        file = inputFile;
        this.startIntOfBlock = startIntOfBlock;
        data = new DataInputStream(new BufferedInputStream(new FileInputStream(inputFile.getFD())));

        advance();
    }

    public void advance() throws IOException, EOFException {
        file.seek(0);
        data.skipBytes(startIntOfBlock*4);
        head = data.readInt();
        System.out.println("StreamBlock: set head to " + this.getHead());
    }

    public int getHead(){
        return this.head;
    }


}
