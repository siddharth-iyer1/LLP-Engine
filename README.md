# LLP-Engine
# Developed by : Siddharth Iyer, Kayvan Mansoorshahi, Varun Arumugam

## Description
A Java Library that standardizes parallelization of algorithms, including Prefix Sum, Bellman-Ford, Prim's, and Optimal BST, through an innovative engine utilizing Lattice Linear Predicate (LLP). This engine facilitates seamless integration, allowing users to input a predicate and adapt it across various algorithm types.

## How to Run

```bash
$ git clone https://github.com/siddharth-iyer1/LLP-Engine.git
$ cd LLP-Engine
$ mvn package
```

This will run random test cases generated by TestCaseGenerator.java.

## Testing

Lists and Graphs are randomly generated in the TestCaseGenerator.java file and example Test Cases generated by this class can be seen in the "test/java/com/The/Boiz/tests" directory.
For direct testing and running of algorithms on individual test cases, I would recommend pasting test cases into the Runner.java file's main method. Example tests are there for context.

## Using the LLP Engine 

This library provides the `Engine<T>` class for running user LLPs. The constructor for the `Engine<T>` class looks like:
```
Engine(BiFunction<Integer, List<T>, T> adv,
       BiFunction<Integer, List<T>, Boolean> B,
       Function<List<Boolean>, Boolean> isDone,
       List<T> globalState, int totalProcs,
       Function<Integer, List<Integer>> cons)
```

`T` is the type of each process (element in the global state). 

`adv` is a BiFunction that defines how each process in the global state advance.

`B` is a BiFunction that defines if a process is "forbidden".

`isDone` allows the user to define when the problem is solved. The input list is a list `L` where `L[i] = isForbidden(i)`. The returned boolean is true if the LLP has completed. We provide `ALL_FINISHED` which simple terminates the program when no processes are forbidden.

`globalState` is the global state for your LLP.

`totalProcs` is the total number of threads created for this problem. Each thread will work on `globalState.size()/totalProcs` processes. 

`cons` is a Function that describes the consumer->producer relationship. This function should accept the process index as input, and return which processes it consumes as output. For example: if `G[i] = G[i+1] + G[i+2]` then `cons(i) = {i+1, i+2}`. We also provide `ALL_CONSUME.apply(n)` which simply makes all processes consume all other processes where `n` is the number of processes (this will reduce performance).

Once all the functions are defined, users can simply create an engine object, and call `<engine_obj>.run()`. This will block the main thread from advancing until the LLP algorithm has concluded. 

We also provide examples for Prefix Sum, Bellman-Ford, Prim's, and Optimal BST in the file `src/main/java/com/The/Boiz/Runner.java`.
