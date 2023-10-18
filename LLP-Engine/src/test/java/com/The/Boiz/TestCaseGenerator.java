package com.The.Boiz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;
import com.The.Boiz.SequentialSolver;

public class TestCaseGenerator {

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        HashMap<List<Integer>, List<Integer>> scanTestCases = generateScanTestCases(50);
        writeToScanTextFile(scanTestCases, "scanTestCases.txt");
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
    
}
