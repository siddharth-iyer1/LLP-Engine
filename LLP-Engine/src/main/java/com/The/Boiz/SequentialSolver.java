package com.The.Boiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
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
        int n = W.size();
        List<Integer> G = new ArrayList<>(n);

        boolean[] visited = new boolean[n];
        visited[0] = true;

        for (int i = 0; i < n; i++) {
            G.add(0);
        }

        for (int i = 1; i < n; i++) {
            int minEdge = Integer.MAX_VALUE;
            int minNode = -1;

            for (int j = 0; j < n; j++) {
            if (visited[j]) {
                for (int k = 0; k < n; k++) {
                if (!visited[k] && W.get(j).get(k) != -1 && W.get(j).get(k) < minEdge) {
                    minEdge = W.get(j).get(k);
                    minNode = k;
                }
                }
            }
            }

            visited[minNode] = true;
            G.set(minNode, minEdge);
        }

        return G;
    }

}
