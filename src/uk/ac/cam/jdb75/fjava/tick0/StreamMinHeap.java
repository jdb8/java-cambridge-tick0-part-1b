package uk.ac.cam.jdb75.fjava.tick0;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public class StreamMinHeap {

    private int[] data;
    //private int[] dataReadCount;
    private int nodeCount = 0;
    private StreamBlock[] streams;
    private int lastAccessedStreamIndex;
    private StreamBlock lastAccessedStream;
    private StreamBlock[] expiredStreams;

    public StreamMinHeap(int size){
        data = new int[size];
        //dataReadCount = new int[size];
        streams = new StreamBlock[size];
        expiredStreams = new StreamBlock[size];
    }

    public int size(){
        return nodeCount;
    }

    public boolean isEmpty(){
        return nodeCount == 0;
    }

    private void swapInt(int[] data, int a, int b){
        int temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }

    private void swapStream(StreamBlock[] data, int a, int b){
        StreamBlock temp = data[a];
        data[a] = data[b];
        data[b] = temp;
    }

    private int min(){
        if (isEmpty()){
            throw new RuntimeException("Heap is currently empty");
        }

        return data[0];
    }

    private void heapifyUp(int index){
        if (index > 0){
            // A parent node has children of 2i+1 and 2i+2, thus
            // index-1/2 will always give the parent (int/int = int)
            int parentIndex = (index-1)/2;

            if (data[parentIndex] > data[index]){
                swapInt(data, index, parentIndex);
                //swapInt(dataReadCount, index, parentIndex);
                swapStream(streams, index, parentIndex);
                heapifyUp(parentIndex);
            }
        }
    }

    public void insert(StreamBlock streamBlock){
        if (nodeCount == data.length){
            throw new RuntimeException("Heap is full");
        }

        data[nodeCount] = streamBlock.getHead();
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

        int smallestChildIndex = (data[leftChildIndex] <= data[rightChildIndex]) ? leftChildIndex : rightChildIndex;

        if (data[index] > data[smallestChildIndex]){
            swapInt(data, index, smallestChildIndex);
            swapStream(streams, index, smallestChildIndex);
            //swapInt(dataReadCount, index, smallestChildIndex);
            // Potentially misplaced element is now at index = smallestChildIndex
            heapify(smallestChildIndex);
        }
    }

    public int removeMin(){
        if (isEmpty()){
            throw new RuntimeException("Heap is currently empty");
        }

        int min = data[0];
        lastAccessedStreamIndex = 0;
        lastAccessedStream = streams[0];
        data[0] = data[nodeCount-1];
        streams[0] = streams[nodeCount-1];


        if (--nodeCount > 0){
            heapify(0);
        }

        return min;
    }

    // this bit is the bit that's wrong, not taking into account if stream is still there, don't just insert
    public void addNextInt(){
        try {
            lastAccessedStream.advance();
            insert(lastAccessedStream);
        } catch (EOFException e) {
            //System.out.println("Stream exhausted");
            System.out.println(e.getMessage());
            expiredStreams[lastAccessedStreamIndex] = lastAccessedStream;
            return;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String toString(){
        int[] readCountArray = new int[expiredStreams.length];
        for (int i = 0; i<nodeCount; i++){
            readCountArray[i] = expiredStreams[i].getReadCount();
        }
        return Arrays.toString(data) + Arrays.toString(readCountArray);
    }

//    /** Test Method */
//    public static void main( String[ ] args ) {
//
//        int[ ] input = { 6, 5, 3, 1, 8, 7, 2, 4 };
//        StreamMinHeap heap = new StreamMinHeap( input.length );
//        for( int i = 0; i < input.length; i++ ) {
//            heap.insert( input[ i ], null );
//        }
//
//        System.out.println(heap);
//    }
}
