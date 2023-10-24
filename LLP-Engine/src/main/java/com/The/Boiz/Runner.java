package com.The.Boiz;

import java.sql.Array;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        int[][] test_case_bellman = {{-1, 80, 34, 61, 98}, {80, -1, 74, 85, 92}, {34, 74, -1, 78, 34}, {61, 85, 78, -1, 91}, {98, 92, 34, 91, -1}};
        for(int i = 0; i < 5; i++){
            List<Integer> w = new ArrayList<Integer>();
            for(int j = 0; j < 5; j++){
                w.add(test_case_bellman[i][j]);
            }
            W.add(w);
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
        int[][] test_case = {{-1, 2, -1, 5, 3}, {2, -1, 9, 6, -1}, {-1, 9 ,-1 ,4, 8}, {5, 6, 4, -1, 7}, {3, -1, 8, 7, -1}};
        for(int i = 0; i < 5; i++){
            List<Integer> w = new ArrayList<Integer>();
            for(int j = 0; j < 5; j++){
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
        System.out.println(d);




        List<Double> W3 = new ArrayList<Double>();
        double[] freq = {2.0,3.0,4.0};
        for(int i = 0; i < 3; i++){
            W3.add(freq[i]);
        }

        Process.resetTID();
        System.out.println("=================== OBST ====================");
        List<Double> h = OBST(W3, 8);
        System.out.println();
        System.out.println(h);

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
                    return Math.max(G.get(2*j + 1), G.get(2*j + 2));
                }
            }
            else {
                try {
                    return Math.addExact(A.get((2*j) - n + 2), A.get((2*j) - n + 3));
                } catch (ArithmeticException e) {
		    System.out.println("Fok2");
                    return Math.max(A.get((2*j) - n + 2), A.get((2*j) - n + 3));
                }
            }
        };

	Function<Integer, List<Integer>> cons = (j) -> {
	    ArrayList<Integer> ret = new ArrayList<Integer>();
            if(j < ((n/2) - 1)) {
		ret.add(2*j + 1);
		ret.add(2*j + 2);
            }
	    return ret;
        };

        // Init Global State
        List<Integer> G = new ArrayList<Integer>();
        for(int i = 0; i < n - 1; i++){
            G.add(Integer.MIN_VALUE);
        }

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs,
							cons);
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

	Function<Integer, List<Integer>> consumes = (j) -> {
	    ArrayList<Integer> ret = new ArrayList<Integer>();
            if(j == 0){
		ret.add(0);
                return ret;
            }
            else if(j % 2 == 1){ // left
                ret.add((j-1)/2); // copy parent
		return ret;
            }
            else{ // right
                if(j < n - 1){
		    ret.add((j-1)/2);
                }
                else{
                    ret.add((j-1)/2);
                }
		return ret;
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

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs,
							consumes);
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

    HashMap<Integer, List<Integer>> pres = new HashMap<Integer, List<Integer>>();
    for(int i = 0; i < n; i++) {
        pres.put(i, pre.apply(i, W));
    }

    Function<Integer, List<Integer>> consumes = (j) -> {
        return pres.get(j);
        };
    
        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (j, G) -> {
        if(j == 0) {
        return false;
        }
            for(Integer i: pres.get(j)) {
        try {
            if(G.get(j) > Math.addExact(G.get(i), W.get(i).get(j)))
            return true;
        } catch(ArithmeticException e) {
            return true;
        }
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (j, G) -> {
            int min = Integer.MAX_VALUE;
            for(int i : pres.get(j)){
        try {
            if(Math.addExact(G.get(i), W.get(i).get(j)) < min) {
            min = G.get(i) + W.get(i).get(j);
            }
        } catch (ArithmeticException e) {
                    continue;
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

        Engine<Integer> llpRunner = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs,
                            consumes);
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
        List<Integer> parent = new ArrayList<Integer>();
        while(parent.size() < n) parent.add(-1);
        parent.set(0, 0);

        BiFunction<Integer, List<Integer>, Boolean> isFixed = (j, G) -> {
            int curr_node = j;
            for(int i = 0; i < n; i++){     // If we go through this n times and don't find v0, it can't be fixed
                curr_node = G.get(curr_node);
                if(curr_node == v0){
                    return true;
                }
        else if(curr_node == -1) { // not connected to fixed
            return false;
        }
            }
            return false;
        };

        BiFunction<Integer, List<Integer>, Boolean> isForbidden = (tid, G) -> {
        if(isFixed.apply(tid, parent)){ // already fixed.
                return false;
            }

        Set<List<Integer>> ePrime = new HashSet<List<Integer>>();
        for(int i = 0; i < n; i++) { // check all nodes
        if(isFixed.apply(i,parent)) {
            // check connected nodes
            for(int j = 0; j < n; j++) {
            if(W.get(i).get(j) != -1 && !isFixed.apply(j, parent)) {
                ePrime.add(Arrays.asList(new Integer[]{i, j}));
            }
            }
        }
        }

        int min = Integer.MAX_VALUE;
        for(List<Integer> entry : ePrime) {
        int index = entry.get(0);
        int val = entry.get(1);
        if(W.get(index).get(val) <= min){
            min = W.get(index).get(val);
        }
        }

        for(List<Integer> entry : ePrime) {
        if(entry.get(1) == tid){
            int index = entry.get(0);
            int val = entry.get(1);
            if(W.get(index).get(val) == min){
            return true;
            }
        }
        }
            return false;
        };

        BiFunction<Integer, List<Integer>, Integer> advance = (tid, G) -> {
            int min = Integer.MAX_VALUE;
            int Gj = 0;
        Set<List<Integer>> ePrime = new HashSet<List<Integer>>();
        for(int i = 0; i < n; i++) {
        if(isFixed.apply(i,parent)) {
            for(int j = 0; j < n; j++) {
            if(W.get(i).get(j) != -1 && !isFixed.apply(j, parent)) {
                ePrime.add(Arrays.asList(new Integer[]{i, j}));
            }
            }
        }
        }
        
            for(List<Integer> entry : ePrime) {
                if(entry.get(1) == tid){
                    int index = entry.get(0);
                    int val = entry.get(1);
            assert val == tid : "fokkkk";
                    if(W.get(index).get(val) <= min){
                        min = W.get(index).get(val);
                        Gj = index;
                    }
                }
            }
        parent.set(tid, Gj);
            return min;
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


        Engine<Integer> llpEngine = new Engine<Integer>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs, (e) -> {return IntStream.range(0, n).boxed().collect(Collectors.toList());});
        llpEngine.run();

        G.set(0,0);

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

        Function<Integer, List<Integer>> consumes = (tid) -> {
            ArrayList<Integer> ret = new ArrayList<Integer>();
                int i = tid / numEles;
                int j = tid % numEles;
                for(int k = i; i < j; k++) {
                    ret.add((i * numEles) + (k-1));
                    ret.add(((k+1) * numEles) + j);
                }
                ret.add((i * numEles) + j);
            return ret;
        };

        BiFunction<Integer, Integer, Double> s = (i, j) -> {
            Double ret = 0.0;
            for(int k = i; k <= j; k++) {
                ret += probs.get(k);
            }
            return ret;
        };

        BiFunction<Integer, List<Double>, Boolean> isForbidden = (tid, globalState) -> {
            int i = tid / numEles;
            int j = tid % numEles;
            Double min = Double.MAX_VALUE;
            for(int k = i; i < j; k++) {
                if(globalState.get((i * numEles) + (k-1)) + s.apply(i, j) + globalState.get(((k+1) * numEles) + j) < min){
                    min = globalState.get((i * numEles) + (k-1)) + s.apply(i, j) + globalState.get(((k+1) * numEles) + j);
                }
            }
            return globalState.get((i * numEles) + j) < min;
        };

        BiFunction<Integer, List<Double>, Double> advance = (tid, globalState) -> {
            int i = tid / numEles;
            int j = tid % numEles;
            Double min = Double.MAX_VALUE;
            for(int k = i; i < j; k++) {
                if(globalState.get((i * numEles) + (k-1)) + s.apply(i, j) + globalState.get(((k+1) * numEles) + j) < min){
                    min = globalState.get((i * numEles) + (k-1)) + s.apply(i, j) + globalState.get(((k+1) * numEles) + j);
                }
            }
            return min;
        };

        Engine<Double> llpEngine = new Engine<Double>(advance, isForbidden, (e) -> { return !e.contains(true);}, G, procs,
                            consumes);
        llpEngine.run();

        return G;
    }
}
