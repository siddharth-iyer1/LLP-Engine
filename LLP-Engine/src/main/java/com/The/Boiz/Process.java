package com.The.Boiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Process<T> extends Thread{
    
    private Boolean done;
    private T curState;
    private List<T> globalState;
    private List<Process<T>> otherThreads;
    private Function<List<T>, T> advance;
    private Function<List<T>, Boolean> B;

    public Process(T startVal, Function<List<T>, T> alpha, Function<List<T>, Boolean> B, List<Process<T>> otherThreads) {
        this.globalState = new ArrayList<T>();
        this.curState = startVal;
        this.B = B;
        this.advance = alpha;
        this.done = false;
        this.otherThreads = otherThreads;
        
    }

    public void Finish() {
        done = true;
    }

    public void updateState(int thread, T newVal) {
        System.out.println("recv " + newVal + " from thread " + thread);
        globalState.set(thread, newVal);
    }

    public T getProcessState() {
        return curState;
    }

    public void run() {
        ThreadId.get(); // init the thread id
        for(Process<T> proc: otherThreads) {
            globalState.add(proc.getProcessState());
        }

        // while forbidden advance
        while(!done) {
            if (B.apply(globalState)) {
                System.out.println(ThreadId.get() + " advanced");
                globalState.set(ThreadId.get(), advance.apply(globalState));
            }
            try {
                Thread.sleep(1000, 0);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            for(Process<T> proc: otherThreads) {
                proc.Finish();
            }
        }
    }

}
