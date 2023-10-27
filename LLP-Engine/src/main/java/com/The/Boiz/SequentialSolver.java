package com.The.Boiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.PriorityQueue;
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
        int V = W.size();

        List<Integer> distance = new ArrayList<>(V);
        boolean[] visited = new boolean[V];

        for (int i = 0; i < V; i++) {
            distance.add(Integer.MAX_VALUE);
            visited[i] = false;
        }

        distance.set(0, 0);

        // Using a priority queue to get the vertex with the minimum distance
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.add(new int[]{0, 0});

        while (!pq.isEmpty()) {
            int u = pq.poll()[0];

            if (visited[u]) continue;
            visited[u] = true;

            for (int v = 0; v < V; v++) {
                if (W.get(u).get(v) != null && !visited[v]
                        && distance.get(u) + W.get(u).get(v) < distance.get(v)) {
                    distance.set(v, distance.get(u) + W.get(u).get(v));
                    pq.add(new int[]{v, distance.get(v)});
                }
            }
        }

        return distance;
    }

    public static List<Integer> seqPrims(List<List<Integer>> W) {
        // Prim's Algorithm given a graph represented by an adjacency matrix
        HashMap<Integer, Integer> addedEdges = new HashMap<Integer, Integer>();
        List<Integer> added_vertices = new ArrayList<Integer>();
        int num_vertices = W.size();

        // Add the first vertex to the set of added vertices
        added_vertices.add(0);
        addedEdges.put(0,0);
        while(added_vertices.size() != num_vertices){
            int min_weight = Integer.MAX_VALUE;
            int min_vertex = -1;

            // find smallest edge crossing the border
            for(int i = 0; i < added_vertices.size(); i++){
                int fixed_vertex = added_vertices.get(i);
                for(int j = 0; j < num_vertices; j++){
                    if(j != fixed_vertex && // no loop
                       W.get(fixed_vertex).get(j) > 0 && // connected
                       !added_vertices.contains(j) && // not already fixed
                       W.get(fixed_vertex).get(j) < min_weight){ 
                        min_weight = W.get(fixed_vertex).get(j);
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
            edges.add(0);
        }
        for(Integer key : addedEdges.keySet()) {
            edges.set(key, addedEdges.get(key));
        }

        return edges;
    }

    public static List<Integer> seqOBST(List<Integer> freq) {
        int n = freq.size();
        List<List<Integer>> cost = new ArrayList<List<Integer>>();

        for (int i = 0; i < n+1; i++) {
            cost.add(new ArrayList<Integer>());
            for (int j = 0; j < n+1; j++) {
                if(i >= n || j >= n) {
                    cost.get(i).add(0);
                } else {
                    cost.get(i).add(i==j ? freq.get(i) : 0);
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
                cost.get(i).set(j, Integer.MAX_VALUE);
                Integer off_set_sum = 0;
                for(int t = i; t <= Math.min(j, n-1); t++) {
                    off_set_sum += freq.get(t);
                }

                // Try making all possible roots 
                for (int r = i; r <= j; r++) 
                { 
                    int c = ((r > i) ? cost.get(i).get(r-1):0) + 
                            ((r < j) ? cost.get(r+1).get(j):0) + 
                            off_set_sum;
                    if (c < cost.get(i).get(j)) 
                        cost.get(i).set(j, c); 
                } 
            } 
        } 

        return cost.get(0);
    }
}