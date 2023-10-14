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
	
	l.add(0);
        for(int i = 0; i < 16; i ++){
            l.add(i);
        }

        // List<Integer> a = reduce(l);
        // Process.resetTID();
        // List<Integer> b = scan(l);
        // System.out.println(l.toString());
        // System.out.println(a.toString());
        // System.out.println(b);

        List<List<Integer>> W = new ArrayList<List<Integer>>();
        for(int i = 0; i < 5; i++) {
            W.add(new ArrayList<Integer>());
            for(int j = 0; j < 5; j++) {
                if(j == i+1)
                    W.get(i).add(1);
                else
                    W.get(i).add(0);
            }
        }
        Process.resetTID();
        System.out.println("================== Bellman ===================");
        List<Integer> c = bellman_ford(W);
        for(List<Integer> t: W) {
            System.out.println(t);
        }
        System.out.println(c);
    }

    public static List<Integer> reduce(List<Integer> A)
    {
        int n = A.size() - 1;

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
	    // j++;
            if(j < (n/2)){
                return G.get(j) < G.get(2*j) + G.get(2*j+1);
            }
            else{
                return G.get(j) < A.get((2*j) - n+1) + A.get((2*j) - n + 2);
            }
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
	    // j++;
            if(j < (n/2) - 1){
		        return G.get(j) + 1;
                // return G.get(2*j) + G.get(2*j+1);
            }
            else{
		        return G.get(j) + 1;
                // return A.get((2*j) - n + 1) + A.get((2*j) - n + 2);
            }
        };

        // Init Global State
        List<Integer> G = new ArrayList<Integer>();
	    List<Process> P = new ArrayList<Process>();
	    G.add(0);
        for(int i = 0; i < n - 1; i++){
            G.add(0);
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
	    System.out.println(G);
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

    public static List<Integer> scan(List<Integer> A){
        List<Integer> S = reduce(A);
        Process.resetTID();
        int n = A.size() - 1;

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            if(j == 1){
                return G.get(j) < 0;
            }
            else if(j % 2 == 0){
                return G.get(j) < G.get(j/2);
            }
            else{
                if(j < n){
                    return G.get(j) < S.get(j - 1) + G.get(j/2);
                }
                else{
                    return G.get(j) < A.get(j - n) + G.get(j/2);
                }
            }
        };
    
        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            if(j == 1){
                return 0;
            }
            else if(j % 2 == 0){
                return G.get(j/2);
            }
            else{
                if(j < n){
                    return S.get(j - 1) + G.get(j/2);
                }
                else{
                    return A.get(j - n) + G.get(j/2);
                }
            }
        };

        List<Integer> G = new ArrayList<Integer>();
	    List<Process> P = new ArrayList<Process>();
	    G.add(0);
        for(int i = 0; i < (2*n) - 1; i++){
            G.add(0);
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
	    System.out.println(G);
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

    public static List<Integer> bellman_ford(List<List<Integer>> W){
        int n = W.size();

        BiFunction<Integer, List<List<Integer>>, List<Integer>> pre = (j, Graph) -> {
            List<Integer> ret = new ArrayList<Integer>();
            for(int i = 0; i < Graph.size(); i++) {
                if(W.get(i).get(j) > 0 && i != j) {
                    ret.add(i);
                }
            }
            return ret;
        };
        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            j--;
            for(Integer i: pre.apply(j, W)) { //TODO: make sure to precompute pre list rather than call every time
                if(G.get(j) > G.get(i) + W.get(i).get(j))
                    return true;
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            j--;
            int min = Integer.MAX_VALUE;
            for(int i : pre.apply(j, W)){
                if(G.get(i) + W.get(i).get(j) < min && G.get(i) != Integer.MAX_VALUE){
                    min = G.get(i) + W.get(i).get(j);
                }
            }
            return min;
        };

        List<Integer> G = new ArrayList<Integer>();
	    List<Process> P = new ArrayList<Process>();
        for(int i = 0; i < n; i++){
            if(i == 0) 
                G.add(0);
            else
                G.add(Integer.MAX_VALUE);
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
	    System.out.println(G);
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
                System.out.println("Horses Ass");
            }
        }
        return G;
    }
// public Process(Integer startVal, BiFunction<Integer, List<Integer>, Integer> alpha, BiFunction<Integer, List<Integer>, Boolean> B, List<Integer> globalState) {

}


