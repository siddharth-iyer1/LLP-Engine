package com.The.Boiz;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class Engine<T> {
    
    private List<Process<T>> allProcs;
    private int totalProcs;
    BiFunction<Integer, List<T>, Boolean> B;
    BiFunction<Integer, List<T>, T> adv;
    Function<List<Boolean>, Boolean> isDone;
    List<T> globalState;
    long startTime;
    long endTime;


    public Engine(BiFunction<Integer, List<T>, T> adv,
                  BiFunction<Integer, List<T>, Boolean> B,
                  Function<List<Boolean>, Boolean> isDone,
                  List<T> globalState, int totalProcs) {
        
        this.totalProcs = totalProcs;
        this.globalState = globalState;
        this.allProcs = new ArrayList<Process<T>>();
        this.isDone = isDone;
        this.B = B;
        this.adv = adv;
        int procs_per_thread = (int)Math.ceil(globalState.size() / (double) totalProcs);

        for(int i = 0; i < totalProcs+1; i++){
            allProcs.add(new Process<T>(adv, B, globalState, procs_per_thread));
        }
        Process.resetTID();
        System.out.println("Launching LLP job with " + 
                           totalProcs +
                           " Processors and " +
                           procs_per_thread +
                           " Processes per thread.");
    }

    public List<T> GetGlobalState() {
        return globalState;
    }

    public long GetRuntime() {
        return endTime - startTime;
    }


    public void run() {
        for(Process<T> p: allProcs) {
            p.start();
        }
        this.startTime = System.nanoTime();
        while(true) {
            if(isDone.apply(allProcs.stream().map(e -> e.isForbidden()).collect(Collectors.toList()))) {
                break;
            }
        }
        this.endTime = System.nanoTime();
        for(Process<T> p: allProcs) {
            p.finish();
            try{
                p.join();
            } catch(InterruptedException e){
                System.out.println("Main thread interrupted while trying to join...");
            }
        }
    }
}
