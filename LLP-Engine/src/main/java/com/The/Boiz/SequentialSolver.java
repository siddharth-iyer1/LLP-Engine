package com.The.Boiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Hello world!
 *
 */
public class SequentialSolver
{

    public static Integer seqReduce(List<Integer> A) {
        int sum = 0;
        for (Integer i: A) {
            sum += i;
        }
        return sum;
    }

    public static List<Integer> seqScan(List<Integer> A) {
        ArrayList<Integer> a = new ArrayList<Integer>();
        int sum = 0;
        for (Integer i: A) {
            a.add(sum);
            sum += i;
        }
        return a;
    }

    public static List<Integer> seqBellmanFord(List<List<Integer>> W) {
        ArrayList<Integer> d = new ArrayList<Integer>();
        for(int i = 0; i < W.size(); i++) {
            d.add(Integer.MAX_VALUE);
        }
        d.set(0, 0);

        BiFunction<Integer, List<List<Integer>>, List<Integer>> pre = (j, Graph) -> {
            List<Integer> ret = new ArrayList<Integer>();
            for(int i = 0; i < Graph.size(); i++) {
                if(W.get(i).get(j) > 0 && i != j) {
                    ret.add(i);
                }
            }
            return ret;
        };

        for(int v = 0; v < W.size(); v++) {
            for (Integer u: pre.apply(v, W)) {
                int temp;
                try {
                    temp = Math.addExact(d.get(u), W.get(u).get(v));
                } catch (ArithmeticException e) {
                    temp = Integer.MAX_VALUE;
                }
                if(d.get(v) > temp) {
                    d.set(v, temp);
                }
            }
        }
        return d;
    }

    public static List<Integer> seqPrims(List<List<Integer>> W) {
        // Prim's Algorithm given a graph represented by an adjacency matrix
        HashMap<Integer, Integer> addedEdges = new HashMap<Integer, Integer>();
        List<Integer> added_vertices = new ArrayList<Integer>();
        int num_vertices = W.size();

        // Add the first vertex to the set of added vertices
        added_vertices.add(0);
        while(added_vertices.size() != num_vertices){
            int min_weight = Integer.MAX_VALUE;
            int min_vertex = -1;
            for(int i = 0; i < added_vertices.size(); i++){
                int vertex = added_vertices.get(i);
                for(int j = 0; j < num_vertices; j++){
                    if(!added_vertices.contains(j) && W.get(vertex).get(j) < min_weight){
                        min_weight = W.get(vertex).get(j);
                        min_vertex = j;
                    }
                }
            }
            added_vertices.add(min_vertex);
            addedEdges.put(min_vertex, min_weight);
        }
        List<Integer> edges = new ArrayList<Integer>();
        // For each item in the HashMap of added edges
        for(int i = 0; i < num_vertices; i++){
            edges.add(-1);
        }
        for(Integer key : addedEdges.keySet()) {
            edges.set(key, addedEdges.get(key));
        }

        return edges;
    }

    public static List<Double> seqOBST(List<Double> freq) {
        int n = freq.size();
        List<List<Double>> cost = new ArrayList<List<Double>>(); 

        for (int i = 0; i < n+1; i++) {
            cost.add(new ArrayList<Double>());
            for (int j = 0; j < n+1; j++) {
                if(i >= n || j >= n) {
                    cost.get(i).add(0.0);
                } else {
                    cost.get(i).add(i==j ? freq.get(i) : 0.0);
                }
            }

        }

        for (int L = 2; L <= n; L++) 
        { 
            // i is row number in cost[][] 
            for (int i = 0; i <= n-L+1; i++) 
            { 
                // Get column number j from row number i and 
                // chain length L 
                int j = i+L-1; 
                cost.get(i).set(j, Double.POSITIVE_INFINITY);
                double off_set_sum = 0;
                for(int t = i; t <= Math.min(j, n-1); t++) {
                    off_set_sum += freq.get(t);
                }
    
                // Try making all possible roots 
                for (int r = i; r <= j; r++) 
                { 
                    double c = ((r > i) ? cost.get(i).get(r-1):0.0) + 
                               ((r < j) ? cost.get(r+1).get(j):0.0) + 
                               off_set_sum;
                    if (c < cost.get(i).get(j)) 
                        cost.get(i).set(j, c); 
                } 
            } 
        } 

        return cost.get(0);
    }
}