package uk.ac.cam.jdb75.fjava.tick0;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

public class StreamMinHeap {

    //private int[] data;
    //private int[] dataReadCount;
    private int nodeCount = 0;
    private StreamBlock[] streams;
    private int lastAccessedStreamIndex;
    private StreamBlock lastAccessedStream;
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

    private int min(){
        if (isEmpty()){
            throw new RuntimeException("Heap is currently empty");
        }

        return streams[0].getHead();
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

        if (streams[index].getHead() > streams[smallestChildIndex].getHead()){
            swapStream(index, smallestChildIndex);
            //swapInt(dataReadCount, index, smallestChildIndex);
            // Potentially misplaced element is now at index = smallestChildIndex
            heapify(smallestChildIndex);
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

    // this bit is the bit that's wrong, not taking into account if stream is still there, don't just insert
//    public void addNextInt(){
//        try {
//            lastAccessedStream.advance();
//            insert(lastAccessedStream);
//        } catch (EOFException e) {
//            //System.out.println("Stream exhausted");
//            System.out.println(e.getMessage());
//            expiredStreams[lastAccessedStreamIndex] = lastAccessedStream;
//            return;
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

    public String toString(){
        int[] readCountArray = new int[expiredStreams.length];
        for (int i = 0; i<nodeCount; i++){
            readCountArray[i] = expiredStreams[i].getReadCount();
        }
        return Arrays.toString(streams) + Arrays.toString(readCountArray);
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
