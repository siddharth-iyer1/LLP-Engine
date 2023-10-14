package com.The.Boiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Process extends Thread{

    private static int nextTID = 0;
    private int myTID;
    private Boolean done;
    private List<Integer> globalState;
    private List<Process> otherThreads;
    private BiFunction<Integer, List<Integer>, Integer> advance;
    private BiFunction<Integer, List<Integer>, Boolean> forbidden;

    public Process(BiFunction<Integer, List<Integer>, Integer> alpha, BiFunction<Integer, List<Integer>, Boolean> B, List<Integer> globalState) {
        this.globalState = globalState; // reference
        this.forbidden = B;
        this.advance = alpha;
        this.done = false;
        this.otherThreads = otherThreads;
	this.myTID = nextTID++;
    }

    public Boolean isForbidden() {
	return forbidden.apply(myTID, globalState);
    }

    public void finish() {
        done = true;
    }

    public void updateState(int thread, Integer newVal) {
        System.out.println("recv " + newVal + " from thread " + thread);
        globalState.set(thread, newVal);
    }

    public Integer getProcessState() {
        return globalState.get(myTID);
    }

    public void run() {
        // while forbidden advance
	System.out.println("Hello from thread: " + myTID + " -> " + Thread.currentThread().getId());
        while(!done) { 
            if (forbidden.apply(myTID, globalState)) {
                globalState.set(myTID+1, advance.apply(myTID, globalState));
            }
        }
    }

}
