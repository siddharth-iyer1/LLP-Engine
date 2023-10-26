package com.The.Boiz;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Engine<T> {

    public static final Function<List<Boolean>, Boolean> ALL_FINISHED = (e) -> { return !e.contains(true);};
    public static final Function<Integer, Function<Integer, List<Integer>>> ALL_CONSUME  = 
                (n) -> { return (e) -> {return IntStream.range(0, n).boxed().collect(Collectors.toList());}; };

    private List<Process<T>> allProcs;
    private int totalProcs;
    BiFunction<Integer, List<T>, Boolean> B;
    BiFunction<Integer, List<T>, T> adv;
    Function<List<Boolean>, Boolean> isDone;
    List<T> globalState;
    ArrayList<Mailbox> mons;
    long startTime;
    long endTime;


    public Engine(BiFunction<Integer, List<T>, T> adv,
                BiFunction<Integer, List<T>, Boolean> B,
                Function<List<Boolean>, Boolean> isDone,
                List<T> globalState, int totalProcs,
                Function<Integer, List<Integer>> cons) 
    {
        this.totalProcs = totalProcs;
        this.globalState = globalState;
        this.allProcs = new ArrayList<Process<T>>();
        this.isDone = isDone;
        this.B = B;
        this.adv = adv;
        int procs_per_thread = Math.max((int)Math.ceil(globalState.size() / (double) totalProcs), 1);
        this.mons = new ArrayList<Mailbox>();

        Map<Integer, List<Integer>> prods = new HashMap<Integer, List<Integer>>();

        for(int i = 0; i < globalState.size(); i++) {
            for(Integer temp: cons.apply(i)) {
            prods.computeIfAbsent(temp, (e) -> new ArrayList<Integer>()).add(i);
            }
        }

        for(int i = 0; i < totalProcs+1; i++){
            allProcs.add(new Process<T>(adv, B, globalState, procs_per_thread, mons, prods));
            mons.add(new Mailbox());
        }
        Process.resetTID();
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
		p.interrupt();
                p.join();
            } catch(InterruptedException e){
                System.out.println("Main thread interrupted while trying to join...");
            }
        }
    }
}
