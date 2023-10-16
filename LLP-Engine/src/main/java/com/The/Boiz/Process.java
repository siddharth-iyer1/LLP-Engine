package com.The.Boiz;

import java.util.List;
import java.util.function.BiFunction;

public class Process extends Thread{

    private static int nextTID = 0;
    private int myTID;
    private Boolean done;
    private List<Integer> globalState;
    private BiFunction<Integer, List<Integer>, Integer> advance;
    private BiFunction<Integer, List<Integer>, Boolean> forbidden;
    private int procs_per_thread;

    public Process(BiFunction<Integer, List<Integer>, Integer> alpha, BiFunction<Integer, List<Integer>, Boolean> B, List<Integer> globalState, int num_procs) {
        this.globalState = globalState; // reference
        this.forbidden = B;
        this.advance = alpha;
        this.done = false;
    	this.myTID = nextTID++;
        procs_per_thread = num_procs;
    }

    public Boolean isForbidden() {
        for(int i = 0; i < procs_per_thread; i++) {
            int localProcNum = myTID*procs_per_thread + i;
	        if (localProcNum < globalState.size() && forbidden.apply(localProcNum, globalState)) {
		    return true;
            }
        }
        return false;
    }

    public void finish() {
        done = true;
    }

    public static void resetTID(){
        nextTID = 0;
    }

    public Integer getProcessState() {
        return globalState.get(myTID);
    }

    public void run() {
        // while forbidden advance
	    // System.out.println("Hello from thread: " + myTID + " -> " + Thread.currentThread().getId());
        while(!done) { 
            for(int i = 0; i < procs_per_thread; i++) {
                int localProcNum = myTID*procs_per_thread + i;
                if (localProcNum < globalState.size() && forbidden.apply(localProcNum, globalState)) {
                    globalState.set(localProcNum, advance.apply(localProcNum, globalState));
                }
            }
        }
    }

}
