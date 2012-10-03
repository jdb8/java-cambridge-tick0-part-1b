package uk.ac.cam.jdb75.fjava.tick0;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public class StreamMinHeap {

    //private int[] data;
    //private int[] dataReadCount;
    private int nodeCount = 0;
    private StreamBlock[] streams;
    private StreamBlock[] expiredStreams;

    public StreamMinHeap(int size){
        //data = new int[size];
        //dataReadCount = new int[size];
        streams = new StreamBlock[size];
        //expiredStreams = new StreamBlock[size];
    }

    public int size(){
        return nodeCount;
    }

    public boolean isEmpty(){
        return nodeCount == 0;
    }

//    private void swapInt(int[] data, int a, int b){
//        int temp = data[a];
//        data[a] = data[b];
//        data[b] = temp;
//    }

    private void swapStream(int a, int b){
        StreamBlock temp = streams[a];
        streams[a] = streams[b];
        streams[b] = temp;
    }

    private void heapifyUp(int index){
        if (index > 0){
            // A parent node has children of 2i+1 and 2i+2, thus
            // index-1/2 will always give the parent (int/int = int)
            int parentIndex = (index-1)/2;

            if (streams[parentIndex].getHead() > streams[index].getHead()){
                //swapInt(data, index, parentIndex);
                //swapInt(dataReadCount, index, parentIndex);
                swapStream(index, parentIndex);
                heapifyUp(parentIndex);
            }
        }
    }

    public void insert(StreamBlock streamBlock){
        if (nodeCount == streams.length){
            throw new RuntimeException("Heap is full");
        }

        streams[nodeCount] = streamBlock;
        //dataReadCount[nodeCount] = streamBlock.getReadCount();
        heapifyUp(nodeCount);
        nodeCount++;
        //System.out.println(toString());
    }

    private void heapify(int index){
        int leftChildIndex = 2*index + 1;
        int rightChildIndex = 2*index + 2;

        if (leftChildIndex >= nodeCount && rightChildIndex >= nodeCount){
            return;
        }

        int smallestChildIndex = (streams[leftChildIndex].getHead() <= streams[rightChildIndex].getHead()) ? leftChildIndex : rightChildIndex;

        while (streams[index].getHead() > streams[smallestChildIndex].getHead()){
            swapStream(index, smallestChildIndex);
            
            leftChildIndex = 2*index + 1;
            rightChildIndex = 2*index + 2;

            if (leftChildIndex >= nodeCount && rightChildIndex >= nodeCount){
                return;
            }

            smallestChildIndex = (streams[leftChildIndex].getHead() <= streams[rightChildIndex].getHead()) ? leftChildIndex : rightChildIndex;
        }
    }

    public StreamBlock removeMin(){
        if (isEmpty()){
            throw new RuntimeException("Heap is currently empty");
        }

        StreamBlock min = streams[0];
        streams[0] = streams[nodeCount-1];


        if (--nodeCount > 0){
            heapify(0);
        }

        return min;
    }

}
