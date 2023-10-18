package com.The.Boiz;

import java.util.List;
import java.util.function.BiFunction;


// assumption: one waiter per monior
public class Mailbox {

    boolean signalledSinceLast;

    Mailbox() {
	signalledSinceLast = false;
    }

    public synchronized void ackSignal() {
	signalledSinceLast = false;
    }

    public synchronized boolean hasPendingSignal() {
	return signalledSinceLast;
    }

    public synchronized void swait() throws InterruptedException{
	if(signalledSinceLast)
	    return;
	super.wait();
    }

    // set flag that a signal came in
    // wake all threads that are already waiting.
    public synchronized void noti() {
	signalledSinceLast = true;
	super.notifyAll();
    }
    
}
