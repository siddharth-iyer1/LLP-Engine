package com.The.Boiz;

import java.util.ArrayList;
import java.util.List;
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
        Process.resetTID();
        List<Integer> b = scan(l);
        System.out.println("input: " + l);
        System.out.println("reduce: " + a);
        System.out.println("scan: " + b);

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
        int n = A.size();

        //    10
        //  3   7
        // 1 2 3 4
        // results in array
        // 10 3 7 
        // the second half of the array should be the sum of children in A
        // G[1] = A[0] + A[1]
        // G[2] = A[2] + A[3]

        // for N = 8
        // First half of list should be processes 0, 1, 2
        // second half should be processes 3, 4, 5, 6
        // 3 -> 0, 1
        // 4 -> 2, 3
        // 5 -> 4, 5
        // 6 -> 6, 7
        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            if(j < ((n/2) - 1)){
                return G.get(j) < G.get(2*j + 1) + G.get(2*j + 2);
            }
            else{
                return G.get(j) < A.get((2*j) - n + 2) + A.get((2*j) - n + 3);
            }
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            if(j < ((n/2) - 1)){
                return G.get(2*j + 1) + G.get(2*j + 2);
            }
            else{
                return A.get((2*j) - n + 2) + A.get((2*j) - n + 3);
            }
        };

        // Init Global State
        List<Integer> G = new ArrayList<Integer>();
	    List<Process> P = new ArrayList<Process>();
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
        int n = A.size();

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            if(j == 0){
                return G.get(j) < 0;
            }
            else if(j % 2 == 1){ // left
                return G.get(j) < G.get((j-1)/2); // copy parent
            }
            else{ // right
                if(j < n - 1){
                    return G.get(j) < S.get(j - 1) + G.get((j-1)/2); // scan[R[v]] = sum[L[v]] + scan[v]
                }
                else{
                    return G.get(j) < A.get(j - n) + G.get((j-1)/2);
                }
            }
        };
    
        // left child = 2*j + 1 (garg's 2*j)
        // right child = 2*j + 2 (garg's 2*j + 1)
        // parent = (j-1)/2
        // using this logic even used to be left child. now odds are left child
        // left child is just copy of parent
        // right child is just copy of parent
        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            if(j == 0){
                return 0;
            }
            else if(j % 2 == 1){ // left
                return G.get((j-1)/2); // copy parent
            }
            else{ // right
                if(j < n - 1){
                    return S.get(j - 1) + G.get((j-1)/2); // scan[R[v]] = sum[L[v]] + scan[v]
                }
                else{
                    return A.get(j - n) + G.get((j-1)/2);
                }
            }
        };

        List<Integer> G = new ArrayList<Integer>();
	    List<Process> P = new ArrayList<Process>();
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
            for(Integer i: pre.apply(j, W)) { //TODO: make sure to precompute pre list rather than call every time
                if(G.get(j) > G.get(i) + W.get(i).get(j))
                    return true;
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
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
}


