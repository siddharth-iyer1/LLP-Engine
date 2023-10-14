package com.The.Boiz;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.BiFunction;

/**
 * Hello world!
 *
 */
public class Runner
{
    public static void main(String[] args )
    {
        List<Integer> l = new ArrayList<Integer>();

        for(int i = 0; i < 16; i ++){
            l.add(i);
        }

        List<Integer> a = reduce(l);
        System.out.println(l.toString());
        System.out.println(a.toString());
    }

    public static List<Integer> reduce(List<Integer> A)
    {
        int n = A.size();

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            if(j >= (n/2) - 1){
                return G.get(j) < G.get(2*j) + G.get(2*j+1);
            }
            else{
                return G.get(j) < A.get((2*j) - n + 1) + A.get((2*j) - n + 2);
            }
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {

            if(j >= (n/2) - 1){
                return G.get(2*j) + G.get(2*j+1);
            }
            else{
                return A.get((2*j) - n + 1) + A.get((2*j) - n + 2);
            }
        };

        // Init Global State
        List<Integer> G = new ArrayList<Integer>();
        List<Process> P = new ArrayList<Process>();
        for(int i = 0; i < n - 1; i++){
            G.add(Integer.MIN_VALUE);
            Process p = new Process(advance, isForbidden, G);
            P.add(p);
        }

        for(Process p : P){
            p.start();
        }

        while(true){
            Boolean allNotForbidden = Boolean.TRUE;
            for(Process p : P){
                if(p.isForbidden()){
                    allNotForbidden = Boolean.FALSE;
                }
            }
            if(allNotForbidden){
                break;
            }
        }

        for(Process p : P){
            p.finish();
        }

        for(Process p : P){
            try{
                p.join();
            }
            catch(InterruptedException e){
                System.out.println("Assballs");
            }
        }
        return G;
    }

// public Process(Integer startVal, BiFunction<Integer, List<Integer>, Integer> alpha, BiFunction<Integer, List<Integer>, Boolean> B, List<Integer> globalState) {

}


