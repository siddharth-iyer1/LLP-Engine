package com.The.Boiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Process extends Thread{
    
    private Boolean done;
    private List<Integer> globalState;
    private List<Process> otherThreads;
    private BiFunction<Integer, List<Integer>, Integer> advance;
    private BiFunction<Integer, List<Integer>, Boolean> forbidden;

    public Process(Integer startVal, BiFunction<Integer, List<Integer>, Integer> alpha, BiFunction<Integer, List<Integer>, Boolean> B, List<Integer> globalState) {
	ThreadId.get(); // get a threadID
        this.globalState = globalState; // reference
        globalState.set(ThreadId.get(), startVal);
        this.forbidden = B;
        this.advance = alpha;
        this.done = false;
        this.otherThreads = otherThreads;
    }

    public Boolean isForbidden() {
	return forbidden.apply(ThreadId.get(), globalState);
    }

    public void Finish() {
        done = true;
    }

    public void updateState(int thread, Integer newVal) {
        System.out.println("recv " + newVal + " from thread " + thread);
        globalState.set(thread, newVal);
    }

    public Integer getProcessState() {
        return globalState.get(ThreadId.get());
    }

    public void run() {
        // while forbidden advance
        while(!done) {
            if (forbidden.apply(ThreadId.get(), globalState)) {
                System.out.println(ThreadId.get() + " advanced");
                globalState.set(ThreadId.get(), advance.apply(ThreadId.get(), globalState));
            }
        }
    }

}
