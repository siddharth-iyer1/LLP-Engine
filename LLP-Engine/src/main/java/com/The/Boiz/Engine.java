package com.The.Boiz;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class Engine {
    
    private List<Process> allProcs;
    private int procs_per_thread;
    BiFunction<Integer, List<Integer>, Boolean> B;
    BiFunction<Integer, List<Integer>, Integer> adv;
    Function<List<Boolean>, Boolean> isDone;
    List<Integer> globalState;
    long startTime;
    long endTime;


    public Engine(BiFunction<Integer, List<Integer>, Integer> adv,
                  BiFunction<Integer, List<Integer>, Boolean> B,
                  Function<List<Boolean>, Boolean> isDone,
                  List<Integer> globalState, int procs_per_thread) {
        
        this.procs_per_thread = procs_per_thread;
        this.globalState = globalState;
        this.allProcs = new ArrayList<Process>();
        this.isDone = isDone;
        this.B = B;
        this.adv = adv;
        int num_threads = (int)Math.ceil(globalState.size() / (double) procs_per_thread);

        for(int i = 0; i < num_threads; i++){
            allProcs.add(new Process(adv, B, globalState, procs_per_thread));
        }
        Process.resetTID();
        System.out.println("Launching LLP job with " + 
                           num_threads +
                           " Threads and " +
                           procs_per_thread +
                           " Processes per thread.");
    }

    public List<Integer> GetGlobalState() {
        return globalState;
    }

    public long GetRuntime() {
        return endTime - startTime;
    }


    public void run() {
        for(Process p: allProcs) {
            p.start();
        }
        this.startTime = System.nanoTime();
        while(true) {
            if(isDone.apply(allProcs.stream().map(e -> e.isForbidden()).collect(Collectors.toList()))) {
                break;
            }
        }
        this.endTime = System.nanoTime();
        for(Process p: allProcs) {
            p.finish();
            try{
                p.join();
            } catch(InterruptedException e){
                System.out.println("Main thread interrupted while trying to join...");
            }
        }
    }
}
