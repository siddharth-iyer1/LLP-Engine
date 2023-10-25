package com.The.Boiz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    final int MAX_PROCS = 1 << 14;
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
    //     int num_tests = 10;
    //     HashMap<List<Integer>, List<Integer>> tests = TestCaseGenerator.generateScanTestCases(num_tests);
    //     for(Map.Entry<List<Integer>, List<Integer>> a : tests.entrySet()) {
    //         int n = a.getKey().size();
    //         List<Integer> l = a.getKey();
    //         List<Integer> res = Runner.reduce(l, Math.min(n, MAX_PROCS));
    //         assertEquals("Reduce Test", a.getKey().stream().mapToInt(e -> e).sum(), res.get(0).intValue()); 
    //     }
    // }

    // public void testPrims() {
    //     System.out.println("========= PRIMS TEST =========");
    //     int num_tests = 10;
    //     HashMap<List<List<Integer>>, List<Integer>> tests = TestCaseGenerator.generateCompleteGraphTestCases(num_tests).get(0); // idx 0 prims tests
    //     for(Map.Entry<List<List<Integer>>, List<Integer>> a : tests.entrySet()) {
    //         int n = a.getKey().size();
    //         List<List<Integer>> W = a.getKey();
    //         List<Integer> res = Runner.prims(W, Math.min(n*n, MAX_PROCS));
    //         assertEquals("Prims Test", a.getValue(), res); 
    //     }
    // }

    // public void testScan() {
    //     System.out.println("========= SCAN TEST =========");
    //     int num_tests = 10;
    //     HashMap<List<Integer>, List<Integer>> tests = TestCaseGenerator.generateScanTestCases(num_tests);
    //     for(Map.Entry<List<Integer>, List<Integer>> a : tests.entrySet()) {
    //         int n = a.getKey().size();
    //         assert (n & (n - 1)) == 0 : "Not power of 2 test";
    //         List<Integer> l = a.getKey();
    //         List<Integer> res = Runner.scan(l, Math.min(2*n, MAX_PROCS));
    //         assertEquals("Scan Test", a.getValue(), res.subList(n-1, 2*n-1)); 
    //     }
    // }

    // public void testBelmanFord() {
    //     System.out.println("========= BELLMAN FORD TEST =========");
    //     int num_tests = 10;
    //     HashMap<List<List<Integer>>, List<Integer>> tests = TestCaseGenerator.generateCompleteGraphTestCases(num_tests).get(1); // idx 0 belman ford tests
    //     for(Map.Entry<List<List<Integer>>, List<Integer>> a : tests.entrySet()) {
    //         int n = a.getKey().size();
    //         List<List<Integer>> W = a.getKey();
    //         List<Integer> res = Runner.bellman_ford(W, Math.min(n*n, MAX_PROCS));
    //         assertEquals("Bellman Ford Test", a.getValue(), res); 
    //     }
    // }

    public void testOBST() {
        System.out.println("========= OBST TEST =========");
        int num_tests = 10;
        HashMap<List<Integer>, List<Integer>> tests = TestCaseGenerator.generateOBSTTestCases(num_tests);
        for(Map.Entry<List<Integer>, List<Integer>> a : tests.entrySet()) {
            int n = a.getKey().size();
            List<Integer> freqs = a.getKey();
            List<Integer> res = Runner.OBST(freqs, Math.min(n*n, MAX_PROCS));
            assertEquals("OBST Test", a.getValue().get(n-1), res.get(n-1)); 
        }
    }
}
