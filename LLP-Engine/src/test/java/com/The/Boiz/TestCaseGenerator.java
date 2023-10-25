package com.The.Boiz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

public class TestCaseGenerator {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        HashMap<List<Integer>, List<Integer>> scanTestCases = generateScanTestCases(50);
        writeToScanTextFile(scanTestCases, "scanTestCases.txt");
        List<HashMap<List<List<Integer>>, List<Integer>>> graphTestCases = generateCompleteGraphTestCases(3);
        writeToGraphTextFile(graphTestCases, "graphTestCases.txt");
    }

    public static List<Integer> generateRandomIntegers() {
        int size = 10 + RANDOM.nextInt(16); // random size between 10 and 25

        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(RANDOM.nextInt(1000)); // assuming you want numbers between 0-999
        }

        return numbers;
    }

    public static HashMap<List<Integer>, List<Integer>> generateScanTestCases(int numberOfTestCases) {
        HashMap<List<Integer>, List<Integer>> scanTestCases = new HashMap<>();

        for (int i = 0; i < numberOfTestCases; i++) {
            List<Integer> input = generateRandomIntegers();
            scanTestCases.put(input, SequentialSolver.seqScan(input));
        }
        return scanTestCases;
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

    private static List<List<Integer>> generateCompleteGraph() {
        List<List<Integer>> adjacencyList = new ArrayList<>();

        // Pick a random number between 25 and 50, this is the number of vertices
        int numberOfVertices = 5;

        for(int i = 0; i < numberOfVertices; i++) {
            List<Integer> vertex = new ArrayList<>();
            for(int j = 0; j < numberOfVertices; j++) {
                if(i == j) {
                    vertex.add(-1);
                } else if(j > i) { // For the upper triangular part
                    int value = 1 + RANDOM.nextInt(100);
                    vertex.add(value);
                } else { // For the lower triangular part
                    vertex.add(adjacencyList.get(j).get(i));
                }
            }
            adjacencyList.add(vertex);
        }
        return adjacencyList;
    }

    public static List<HashMap<List<List<Integer>>, List<Integer>>> generateCompleteGraphTestCases(int numberOfTestCases) {
        HashMap<List<List<Integer>>, List<Integer>> primsTestCases = new HashMap<>();
        HashMap<List<List<Integer>>, List<Integer>> bellmanFordTestCases = new HashMap<>();
        for(int i = 0; i < numberOfTestCases; i++) {
            List<List<Integer>> graph = generateCompleteGraph();
            primsTestCases.put(graph, SequentialSolver.seqPrims(graph));
        }
        for(int i = 0; i < numberOfTestCases; i++){
            List<List<Integer>> graph = generateCompleteGraph();
            bellmanFordTestCases.put(graph, SequentialSolver.seqBellmanFord(graph));
        }
        List<HashMap<List<List<Integer>>, List<Integer>>> testCases = new ArrayList<>();
        testCases.add(primsTestCases);
        testCases.add(bellmanFordTestCases);
        return testCases;
    }

    public static void writeToGraphTextFile(List<HashMap<List<List<Integer>>, List<Integer>>> graphTestCases, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for(List<List<Integer>> graph : graphTestCases.get(0).keySet()) {
                    writer.write("Input Graph: ");
                    // Replace [ with { and ] with } to make it a set
                    String graphString = graph.toString();
                    graphString = graphString.replace('[', '{');
                    graphString = graphString.replace(']', '}');
                    writer.write(graphString);
                    writer.newLine();

                    writer.write("Expected Output, Prim's: ");
                    List<Integer> output = graphTestCases.get(0).get(graph);
                    String outputString = output.toString();
                    outputString = outputString.replace('[', '{');
                    outputString = outputString.replace(']', '}');
                    writer.write(outputString);
                    writer.newLine();
                    writer.newLine();
                }
                for(List<List<Integer>> graph2 : graphTestCases.get(1).keySet()) {
                    writer.write("Input Graph: ");
                    // Replace [ with { and ] with } to make it a set
                    String graph2String = graph2.toString();
                    graph2String = graph2String.replace('[', '{');
                    graph2String = graph2String.replace(']', '}');
                    writer.write(graph2String);
                    writer.newLine();

                    writer.write("Expected Output, Bellman-Ford: ");
                    List<Integer> bOutput = graphTestCases.get(1).get(graph2);
                    String bOutputString = bOutput.toString();
                    bOutputString = bOutputString.replace('[', '{');
                    bOutputString = bOutputString.replace(']', '}');
                    writer.write(bOutputString);
                    writer.newLine();
                    writer.newLine();
                }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}