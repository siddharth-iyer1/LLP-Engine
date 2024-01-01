package com.The.Boiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest
    extends TestCase
{
    final int MAX_PROCS = 1 << 6;
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

    // public void testReduce() {
    //     System.out.println("========= REDUCE TEST =========");
    //     System.out.println("Warning: Long runtime expected...");
    //     int num_tests = 10;
    //     int count = 1;
    //     HashMap<List<Integer>, Pair<List<Integer>, Long>> tests = TestCaseGenerator.generateScanTestCases(num_tests);
    //     for(Map.Entry<List<Integer>, Pair<List<Integer>, Long>> a : tests.entrySet()) {
    //         int n = a.getKey().size();
    //         List<Integer> l = a.getKey();
    //         Pair<List<Integer>, Engine<Integer>> res = Runner.reduce(l, Math.min(n, MAX_PROCS));
    //         System.out.println("TEST" + count + ":");
    //         System.out.println("\tLLP Time:     " + res.second.GetRuntime() + " ns");
    //         long startTime = System.nanoTime();
    //         int exp = a.getKey().stream().mapToInt(e -> e).sum();
    //         long endTime = System.nanoTime();
    //         System.out.println("\tStreams Time: " + (endTime - startTime) + " ns");
    //         assertEquals("Reduce Test", exp, res.first.get(0).intValue()); 
    //         count++;
    //     }
    //     System.out.println("All test cases PASSED!\n");
    // }

    public void testPrims() {
        System.out.println("========= PRIMS TEST =========");
        int num_tests = 10;
        int count = 1;
        HashMap<List<List<Integer>>, Pair<List<Integer>, Long>> tests = TestCaseGenerator.generateCompleteGraphTestCases(num_tests).get(0); // idx 0 prims tests
        for(Map.Entry<List<List<Integer>>, Pair<List<Integer>, Long>> a : tests.entrySet()) {
            int n = a.getKey().size();
            List<List<Integer>> W = a.getKey();
            Pair<List<Integer>, Engine<Integer>> res = Runner.prims(W, Math.min(n*n, MAX_PROCS));
            System.out.println("TEST " + count + ":");
            System.out.println("\tLLP Time: " + res.second.GetRuntime() + " ns");
            System.out.println("\tSeq Time: " + a.getValue().second + " ns");
            assertEquals("Prims Test", a.getValue().first, res.first); 
            count++;
        }
        System.out.println("All test cases PASSED!\n");
    }

    public void testScan() {
        System.out.println("========= SCAN TEST =========");
        System.out.println("Warning: Long runtime expected...");
        int num_tests = 10;
        int count = 1;
        HashMap<List<Integer>, Pair<List<Integer>, Long>> tests = TestCaseGenerator.generateScanTestCases(num_tests);
        for(Map.Entry<List<Integer>, Pair<List<Integer>, Long>> a : tests.entrySet()) {
            int n = a.getKey().size();
            assert (n & (n - 1)) == 0 : "Not power of 2 test";
            List<Integer> l = a.getKey();
            Pair<List<Integer>, Engine<Integer>> res = Runner.scan(l, Math.min(2*n, MAX_PROCS));
            System.out.println("TEST " + count + ":");
            System.out.println("\tLLP Time: " + res.second.GetRuntime() + " ns");
            System.out.println("\tSeq Time: " + a.getValue().second + " ns");
            assertEquals("Scan Test", a.getValue().first, res.first.subList(n-1, 2*n-1)); 
            count++;
        }
        System.out.println("All test cases PASSED!\n");
    }

    public void testBelmanFord() {
        System.out.println("========= BELLMAN FORD TEST =========");
        int num_tests = 10;
        int count = 1;
        HashMap<List<List<Integer>>, Pair<List<Integer>, Long>> tests = TestCaseGenerator.generateCompleteGraphTestCases(num_tests).get(1); // idx 0 belman ford tests
        for(Map.Entry<List<List<Integer>>, Pair<List<Integer>, Long>> a : tests.entrySet()) {
            int n = a.getKey().size();
            List<List<Integer>> W = a.getKey();
            Pair<List<Integer>, Engine<Integer>> res = Runner.bellman_ford(W, Math.min(n*n, MAX_PROCS));
            System.out.println("TEST " + count + ":");
            System.out.println("\tLLP Time: " + res.second.GetRuntime() + " ns");
            System.out.println("\tSeq Time: " + a.getValue().second + " ns");
            assertEquals("Bellman Ford Test", a.getValue().first, res.first); 
            count++;
        }
        System.out.println("All test cases PASSED!\n");
    }

    public void testOBST() {
        System.out.println("========= OBST TEST =========");
        int num_tests = 10;
        HashMap<List<Integer>, Pair<List<Integer>, Long>> tests = TestCaseGenerator.generateOBSTTestCases(num_tests);
        int count = 1;
        for(Map.Entry<List<Integer>, Pair<List<Integer>, Long>> a : tests.entrySet()) {
            int n = a.getKey().size();
            List<Integer> freqs = a.getKey();
            Pair<List<Integer>, Engine<Integer>> res = Runner.OBST(freqs, Math.min(n*n, MAX_PROCS));
            System.out.println("TEST " + count + ":");
            System.out.println("\tLLP Time: " + res.second.GetRuntime() + " ns");
            System.out.println("\tSeq Time: " + a.getValue().second + " ns");
            assertEquals("OBST Test", a.getValue().first.get(n-1), res.first.get(n-1)); 
            count++;
        }
        System.out.println("All test cases PASSED!\n");
    }
}
