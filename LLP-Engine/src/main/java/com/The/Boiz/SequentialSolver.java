package com.The.Boiz;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
                int temp = d.get(v) + W.get(v).get(u);
                if(d.get(v) > temp) {
                    d.set(v, temp);
                }
            }
        }
        return d;
    }
}


