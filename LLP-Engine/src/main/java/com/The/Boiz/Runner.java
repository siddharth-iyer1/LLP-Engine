package com.The.Boiz;

import java.sql.Array;
import java.util.*;
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

        List<Integer> a = reduce(l, 8);
        List<Integer> b = scan(l, 8);
        System.out.println("============== Parallel Prefix ===============");
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
        List<Integer> c = bellman_ford(W, 8);
        for(List<Integer> t: W) {
            System.out.println(t);
        }
        System.out.println();
        System.out.println(c);

        List<List<Integer>> W2 = new ArrayList<List<Integer>>();
        int[][] test_case ={
                {-1, 2, 3, 3, -1, -1, -1},
                {2, -1, 4, -1, 3, -1, -1},
                {3, 4, -1, 5, 1, 6, -1},
                {3, -1, 5, -1, -1, 7, -1},
                {-1, 3, 1, -1, -1, 8, -1},
                {-1, -1, 6, 7, 8, -1, 8},
                {-1, -1, -1, -1, -1, 8, -1}
        };
        for(int i = 0; i < 7; i++){
            List<Integer> w = new ArrayList<Integer>();
            for(int j = 0; j < 7; j++){
                w.add(test_case[i][j]);
            }
            W2.add(w);
        }

        Process.resetTID();
        System.out.println("=================== Prim's ====================");
        List<Integer> d = prims(W2, 8);
        for(List<Integer> t: W2) {
            System.out.println(t);
        }
        System.out.println();
        System.out.println(c);
    }

    public static List<Integer> reduce(List<Integer> A, int procs)
    {
        int n = A.size();
        Process.resetTID();

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
                try {
                    return G.get(j) < Math.addExact(G.get(2*j + 1), G.get(2*j + 2));
                } catch (ArithmeticException e) {
                    return true;
                }
            }
            else{
                try {
                    return G.get(j) < Math.addExact(A.get((2*j) - n + 2), A.get((2*j) - n + 3));
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                    System.out.println(A.get((2*j) - n + 2) + " " + A.get((2*j) - n + 3));
                    return true;
                }
            }
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            if(j < ((n/2) - 1)) {
                try {
                    return Math.addExact(G.get(2*j + 1), G.get(2*j + 2));
                } catch (ArithmeticException e) {
                    return Math.min(G.get(2*j + 1), G.get(2*j + 2));
                }
            }
            else {
                try {
                    return Math.addExact(A.get((2*j) - n + 2), A.get((2*j) - n + 3));
                } catch (ArithmeticException e) {
                    return Math.min(A.get((2*j) - n + 2), A.get((2*j) - n + 3));
                }
            }
        };

        // Init Global State
        List<Integer> G = new ArrayList<Integer>();
        for(int i = 0; i < n - 1; i++){
            G.add(Integer.MIN_VALUE);
        }

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs);
        llpRunner.run();

        System.out.println("Reduce LLP time: " + llpRunner.GetRuntime() + " ns");

        return llpRunner.GetGlobalState();
    }

    public static List<Integer> scan(List<Integer> A, int procs){
        List<Integer> S = reduce(A, procs);
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
                    try {
                        return G.get(j) < Math.addExact(S.get(j - 1), G.get((j-1)/2)); // scan[R[v]] = sum[L[v]] + scan[v]
                    } catch (ArithmeticException e) {
                        return true;
                    }
                }
                else{
                    try{
                        return G.get(j) < Math.addExact(A.get(j - n), G.get((j-1)/2));
                    } catch (ArithmeticException e) {
                        return true;
                    }
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
                    try {
                        return Math.addExact(S.get(j - 1), G.get((j-1)/2)); // scan[R[v]] = sum[L[v]] + scan[v]
                    } catch (ArithmeticException e) {
                        return Integer.MIN_VALUE;
                    }
                }
                else{
                    try {
                        return Math.addExact(A.get(j - n), G.get((j-1)/2));
                    } catch (ArithmeticException e) {
                        return Integer.MIN_VALUE;
                    }
                }
            }
        };

        List<Integer> G = new ArrayList<Integer>();
        for(int i = 0; i < (2*n) - 1; i++){
            G.add(Integer.MIN_VALUE);
        }

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs);
        llpRunner.run();

        System.out.println("Scan LLP time  : " + llpRunner.GetRuntime() + " ns");

        return llpRunner.GetGlobalState();
    }

    public static List<Integer> bellman_ford(List<List<Integer>> W, int procs){
        int n = W.size();
        Process.resetTID();

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
        for(int i = 0; i < n; i++){
            if(i == 0) 
                G.add(0);
            else
                G.add(Integer.MAX_VALUE);
        }

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs);
        llpRunner.run();

        System.out.println("BellF LLP time : " + llpRunner.GetRuntime() + " ns");

        return llpRunner.GetGlobalState();    }

    public static List<Integer> prims(List<List<Integer>> W, int procs){

//        var G: array[1..n-1] of real; Initially all i : G[i] = minimum edge adjacent to i;
//        always
//          fixed(j, G) means there exists a directed path from vj to v0 using edges in G
//          E' := { (i, k) in E | fixed(i, G) ^ ¬fixed(k, G)};
//          forbidden (j) means There exists some i : (i, j) in E' such that it has minimum weight w[i, j] of all edges in E 0
//        advance(j) G[j] := min{w[i, j] | (i, j) 2 E 0 }

//        We define a vertex to be fixed if by traversing the path starting from the edge proposed by
//        that vertex leads to v 0

        // In this implementation, we're going to have the indices (i) of our Global state contain the
        // Node who is pointing to Node i

        // Meaning G[i] = j, means (j, i) is a part of the Edge Set
        // j -> i for clarification

        int n = W.size();
        int v0 = 0;     // Arbitrarily set the initial vertex to the 0th vertex
        HashMap<Integer, Integer> e_prime = new HashMap<Integer, Integer>();
        Process.resetTID();

        BiFunction<Integer, List<Integer>, Boolean> isFixed = (j, G) -> {
            int curr_node = j;
            for(int i = 0; i < n; i++){     // If we go through this n times and don't find v0, it can't be fixed
                curr_node = G.get(curr_node);
                if(curr_node == v0){
                    return true;
                }
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Boolean> isE_Prime = (j, G) -> {
            if(W.get(j).get(G.get(j)) == -1){   // If our Global State has index j pointed to by a null, not in E
                return false;
            }
            else{
                Boolean condition1 = isFixed.apply(G.get(j), G);
                Boolean condition2 = !isFixed.apply(j, G);

                if(condition1 && condition2){
                    return true;
                }
                else{
                    return false;
                }
            }
        };

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
            // If j is fixed, return false
            // For all predecessors
                // Check if predecessor has the minimum weight of all edges in E'
                // If in E'

            HashMap<Integer, Integer> local_e_prime = new HashMap<>();
            for(int k : G){
                if(isE_Prime.apply(k, G)){
                    local_e_prime.put(G.get(k), k);
                }
            }

            if(isE_Prime.apply(j, G)){
                return false;
            }
            else{
                int min = Integer.MAX_VALUE;
                for(Map.Entry<Integer, Integer> entry : e_prime.entrySet()) {
                    int index = entry.getKey();
                    int val = entry.getValue();
                    if(W.get(index).get(val) <= min){
                        min = W.get(index).get(val);
                    }
                }

                for(Map.Entry<Integer, Integer> entry : e_prime.entrySet()) {
                    if(entry.getValue() == j){
                        int index = entry.getKey();
                        int val = entry.getValue();
                        if(W.get(index).get(val) == min){
                            return true;
                        }
                    }
                }
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            int min = Integer.MAX_VALUE;
            int Gj = 0;
            for(Map.Entry<Integer, Integer> entry : e_prime.entrySet()) {
                if(entry.getValue() == j){
                    int index = entry.getKey();
                    int val = entry.getValue();
                    if(W.get(index).get(val) <= min){
                        min = W.get(index).get(val);
                        Gj = index;
                    }
                }
            }
            return Gj;
        };





        // Setup Global State and run

        List<Integer> G = new ArrayList<Integer>();

        for(int i = 0; i < n; i++){
            int min = Integer.MAX_VALUE;
            int min_index = 0;
            for(int j = 0; j < W.get(i).size(); j++){
                if(W.get(i).get(j) <= min){
                    min = W.get(i).get(j);
                    min_index = j;
                }
            }
            G.add(min_index);
        }

        
        Engine<Integer> llpEngine = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs);
        llpEngine.run();

        return G;
    }

    public static List<Double> OBST(List<Double> probs, int procs) {
        // input: probs, frequency of each symbol
        // init G[i, j] = 0; G[i, i] = probs[i]
        // always s(i,j) = sum probs from i to j
        // ensure: G[i, j] >= min {G[i,k-1] + s(i,j) + G[k+1, j]} where k is in range [i, j)
        // priority: (j - i) ?????? seems like a scheduling opt
        // https://dl.acm.org/doi/pdf/10.1145/3491003.3491019 gargs is goated.

        // init global state
        int numEles = probs.size();

        List<Double> G = new ArrayList<Double>();
        for(int i = 0; i < numEles; i++) {
            for(int j = 0; j < numEles; j++) {
                if(i == j) {
                    G.add(probs.get(i));
                }
                else {
                    G.add(0.0);
                }
            }
        }

        BiFunction<Integer, Integer, Double> s = (i, j) -> {
            Double ret = 0.0;
            for(int k = i; k <= j; k++) {
                ret += probs.get(k);
            }
            return ret;
        };

        BiFunction<Integer, List<Double>, Boolean> isForbidden = (tid, globalState) -> {
            ArrayList<Double> temp = new ArrayList<Double>();
            int n = globalState.size();
            int i = tid / n;
            int j = tid % n;
            for(int k = i; i < j; i++) {
                temp.add(globalState.get(i * n + k-1) + 
                         s.apply(i, j) + 
                         globalState.get((k+1) * n + j));
            }
            Double min = Collections.min(temp);
            return globalState.get(i * n + j) < min;
        };

        BiFunction<Integer, List<Double>, Double> advance = (tid, globalState) -> {
            ArrayList<Double> temp = new ArrayList<Double>();
            int n = globalState.size();
            int i = tid / n;
            int j = tid % n;
            for(int k = i; i < j; i++) {
                temp.add(globalState.get(i * n + k-1) + 
                         s.apply(i, j) + 
                         globalState.get((k+1) * n + j));
            }
            Double min = Collections.min(temp);
            return min;
        };

        Engine<Double> llpEngine = new Engine<Double>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs);
        llpEngine.run();

        return G;
    }

}


