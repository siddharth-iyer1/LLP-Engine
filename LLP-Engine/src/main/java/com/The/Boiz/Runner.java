package com.The.Boiz;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Hello world!
 *
 */
public class Runner 
{
    public static void main( String[] args )
    {
        System.out.println( "Heya from Runner!" );

        Function<List<Integer>, Integer> a = (t) -> t.get(0);
        Function<List<Integer>, Boolean> b = (t) -> true;
        ArrayList<Thread> TP = new ArrayList<Thread>();
        ArrayList<Process<Integer>> Procs = new ArrayList<Process<Integer>>();

        for(int i = 0; i < 20; i++) {
            Process<Integer> temp = new Process<Integer>(0, a, b, Procs);
            Procs.add(temp);
            if(i % 2 == 0) 
                temp.Finish();
            TP.add(new Thread(temp));
        }
        System.out.println("Launching Jobs...");
        for(Thread t: TP){
            t.start();
        }

        try {
            for(Thread t: TP){
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("The Main thread is interrupted");
        }
    }
}
