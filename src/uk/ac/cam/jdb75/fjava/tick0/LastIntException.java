package uk.ac.cam.jdb75.fjava.tick0;

public class LastIntException extends Exception {

    int lastInt;

    public LastIntException(int lastInt){
        this.lastInt = lastInt;
    }
}
