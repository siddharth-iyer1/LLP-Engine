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

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        testReduce();
    }

    public void testReduce() {
        Random rand = new Random();
        for (int n = (1 << 4); n <= (1 << 20); n *= 2) {
            System.out.println("Reduce test size " + n);
            List<Integer> l = new ArrayList<Integer>();
            for(int i = 0; i < n; i++) {
                l.add(i);
            }
            long sst = System.nanoTime();
            Integer seq = SequentialSolver.seqReduce(l);
            long set = System.nanoTime();

            List<Integer> a = Runner.reduce(l);
            System.out.println("sequ time: " + (set - sst) + "ns");
            assertEquals("Reduce " + n, seq, a.get(0)); 

        }
    }
}
