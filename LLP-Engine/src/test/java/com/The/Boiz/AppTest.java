package com.The.Boiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    public void testReduce() {
        System.out.println("========= REDUCE TEST =========");
        Random rand = new Random();
        int n = 1 << 10;
	int procs = 1 << 13;
        System.out.println("Reduce test size " + n);
        List<Integer> l = new ArrayList<Integer>();
        for(int i = 0; i < n; i++) {
            l.add(128);
        }
        List<Integer> a = Runner.reduce(l, Math.min(procs, n));

        long sst = System.nanoTime();
        Integer seq = SequentialSolver.seqReduce(l);
        long set = System.nanoTime();

        System.out.println("sequ time      : " + (set - sst) + " ns");
        assertEquals("Reduce " + n, seq, a.get(0)); 
    }

    public void testScan() {
        System.out.println("========= SCAN TEST =========");
        Random rand = new Random();
        int n = 1 << 10;
	int procs = 1 << 13;
        System.out.println("Scan test size " + n);
        List<Integer> l = new ArrayList<Integer>();
        for(int i = 0; i < n; i++) {
            l.add(i % 128);
        }
        List<Integer> a = Runner.scan(l, Math.min(procs, n));

        long sst = System.nanoTime();
        List<Integer> seq = SequentialSolver.seqScan(l);
        long set = System.nanoTime();

        System.out.println("sequ time      : " + (set - sst) + " ns");
        assertEquals("Scan", seq, a.subList(n-1, 2*n-1)); 
    }

    public void testBelmanFord() {
        System.out.println("========= BELLMAN FORD TEST =========");
        Random rand = new Random();
        int n = 1 << 10;
	int procs = 1 << 13;
        System.out.println("Bellman ford test size " + n);
        List<List<Integer>> l = new ArrayList<List<Integer>>();
        for(int i = 0; i < n; i++) {
            l.add(new ArrayList<Integer>());
            for(int j = 0; j < n; j++){
                if(j == (i + 1)%n) {
                    l.get(i).add(1);
                }
                else {
                    l.get(i).add(0);
                }
            }
        }
        List<Integer> a = Runner.bellman_ford(l, Math.min(procs, n));
	System.out.println("LLP Done");

        long sst = System.nanoTime();
        List<Integer> seq = SequentialSolver.seqBellmanFord(l);
        long set = System.nanoTime();

        System.out.println("sequ time      : " + (set - sst) + " ns");
        assertEquals("Bellman Ford", seq, a); 
    }

}
