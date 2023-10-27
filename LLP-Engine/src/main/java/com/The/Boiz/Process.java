package com.The.Boiz;

import java.util.List;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.Map;

public class Process<T> extends Thread {

	private static int nextTID = 0;
	private int myTID;
	private Boolean done;
	private volatile List<T> globalState;
	private BiFunction<Integer, List<T>, T> advance;
	private BiFunction<Integer, List<T>, Boolean> forbidden;
	private Map<Integer, List<Integer>>cons;
	private List<Mailbox> mons;
	private int procs_per_thread;

	public Process(BiFunction<Integer, List<T>, T> alpha, BiFunction<Integer, List<T>, Boolean> B, List<T> globalState, int num_procs,
		List<Mailbox> mons, Map<Integer, List<Integer>> cons) {
		this.globalState = globalState; // reference
		this.forbidden = B;
		this.advance = alpha;
		this.done = false;
		this.myTID = nextTID++;
	this.mons = mons; // monitors
	this.cons = cons;
		procs_per_thread = num_procs;
	}

	public Boolean isForbidden() {
		for(int i = 0; i < procs_per_thread; i++) {
			int localProcNum = myTID*procs_per_thread + i;
			if (localProcNum < globalState.size() && forbidden.apply(localProcNum, globalState)) {
			return true;
			}
		}
		return false;
	}

	public void finish() {
		done = true;
	}

	public static void resetTID(){
		nextTID = 0;
	}

	public T getProcessState() {
		return globalState.get(myTID);
	}

	public void run() {
        ArrayList<Integer> updatedProcesses;
		while(!done) {
            updatedProcesses = new ArrayList<Integer>();

		    // check forbidden, and advance.
			for(int i = 0; i < procs_per_thread; i++) {
				int localProcNum = myTID*procs_per_thread + i;
				if (localProcNum < globalState.size() && forbidden.apply(localProcNum, globalState)) {
					globalState.set(localProcNum, advance.apply(localProcNum, globalState));
			updatedProcesses.add(localProcNum);
				}
			}

            // sync scheme. Each thread has one monitor.
            // when a producer creates a value to be consumed it wakes the thread's monitor
            // after an advance we are no longer forbidden, so we wait until someone wakes us up.

            // this doesn't work now. case to conside:
            // thread 0 and 1 are running in lock step.
            // thread 1 produces a value for thread 0.
            // thread 1, and 0 both update a process.
            // thread 1 notifies thread 0's monitor, but thread 0 is NOT waiting yet. Thus thread 0 never stops waiting.
            // need to notify thread 0 if waiting, OR let thread 0 know that there is an update so it doesn't wait...
            // mailbox? refactor to message passing? 

            // for all the states we updated, notify their monitors.
            for(Integer i: updatedProcesses) {
                // need a map from prod process to cons thread
                List<Integer> consumers = cons.get(i);
                if(consumers != null) {
                    for(Integer temp: consumers) {
                        temp = temp / procs_per_thread;
                        // System.out.println(myTID + " notifies thread " + temp + " for val " + i);
                        Mailbox monitor = mons.get(temp); // TODO: need to map a process to a consumer thread.
                        synchronized(monitor) {
                            monitor.noti();
                        }
                    }
                }
		    }

            Mailbox  myMonitor = mons.get(myTID);
            // if someone already signalled for a wakeup, then skip waiting.
            if(!myMonitor.hasPendingSignal()) {
                synchronized(myMonitor) {
                    try {
                    // System.out.println(myTID + " is sleeping...");
                    myMonitor.swait();
                    } catch (InterruptedException e) {
                    // System.out.println(myTID + " Thread interrupted while waiting...");
                    }
                    // System.out.println(myTID + " woke up!");
                }
            }
            else {
                // System.out.println(myTID + " skipped sleeping!");
            }
            myMonitor.ackSignal();
		}
	}

}
