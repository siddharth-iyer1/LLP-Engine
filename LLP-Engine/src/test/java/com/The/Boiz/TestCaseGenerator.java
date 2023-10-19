package com.The.Boiz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import com.The.Boiz.SequentialSolver;

// I was having issues writing the file to the tests folder so I just wrote the files then moved them.

public class TestCaseGenerator {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        HashMap<List<Integer>, List<Integer>> scanTestCases = generateScanTestCases(50);
        writeToScanTextFile(scanTestCases, "scanTestCases.txt");
        HashMap<List<List<Integer>>, List<Integer>> graphTestCases = generateCompleteGraphTestCases(50);

    }

    public static HashMap<List<Integer>, List<Integer>> generateScanTestCases(int numberOfTestCases) {
        HashMap<List<Integer>, List<Integer>> scanTestCases = new HashMap<>();

        for (int i = 0; i < numberOfTestCases; i++) {
            List<Integer> input = generateRandomIntegers();
            scanTestCases.put(input, SequentialSolver.seqScan(input));
        }
        return scanTestCases;
    }

    public static List<Integer> generateRandomIntegers() {
        int size = 10 + RANDOM.nextInt(16); // random size between 10 and 25

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(RANDOM.nextInt(1000)); // assuming you want numbers between 0-999
        }

        return numbers;
    }

    public static void writeToScanTextFile(HashMap<List<Integer>, List<Integer>> scanTestCases, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (List<Integer> input : scanTestCases.keySet()) {
                // Write the input list
                writer.write("Input: [");
                for (int i = 0; i < input.size(); i++) {
                    if (i != 0) {
                        writer.write(",");
                    }
                    writer.write(String.valueOf(input.get(i)));
                }
                writer.write("]");
                writer.newLine();
                writer.newLine();
    
                writer.write("Expected Output: [");
                // Write the corresponding output from the prefix scan
                List<Integer> output = scanTestCases.get(input);
                for (int i = 0; i < output.size(); i++) {
                    if (i != 0) {
                        writer.write(",");
                    }
                    writer.write(String.valueOf(output.get(i)));
                }
                writer.write("]");
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeToGraphTextFile(HashMap<List<List<Integer>>, List<Integer>> graphTestCases, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for(List<List<Integer>> graph : graphTestCases.keySet()) {
                writer.write(graph.toString());
                writer.newLine();
                writer.newLine();

                writer.write("Expected Output, Prim's: ");
                List<Integer> output = graphTestCases.get(graph);
                writer.write(output.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Write me a java program that generates 50 complete graphs with 10-25 vertices and writes them to a file called completeGraphTestCases.txt
    public static HashMap<List<List<Integer>>, List<Integer>> generateCompleteGraphTestCases(int numberOfTestCases) {
        HashMap<List<List<Integer>>, List<Integer>> testCases = new HashMap<>();
        for (int i = 0; i < numberOfTestCases; i++) {
            int vertices = 10 + RANDOM.nextInt(16); // Random number between 10 and 25
            List<List<Integer>> graph = generateCompleteGraph(vertices);
            testCases.put(graph, SequentialSolver.seqPrims(graph));

        }
        return testCases;
    }

    private static List<List<Integer>> generateCompleteGraph(int vertices) {
        List<List<Integer>> adjacencyList = new ArrayList<>();
        
        // To ensure unique edge weights
        Set<Integer> usedWeights = new HashSet<>();

        for (int i = 0; i < vertices; i++) {
            List<Integer> edges = new ArrayList<>();
            for (int j = i + 1; j < vertices; j++) {
                int weight;
                do {
                    weight = RANDOM.nextInt(1000); // Assuming edge weights between 0-999
                } while (usedWeights.contains(weight));
                usedWeights.add(weight);
                
                edges.add(weight);
            }
            adjacencyList.add(edges);
        }

        return adjacencyList;
    }
}

