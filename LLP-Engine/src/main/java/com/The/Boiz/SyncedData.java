package com.The.Boiz;


public class SyncedData<T> {
    volatile T data;

    public SyncedData(T initValue) {
	this.data = data;
    }

    public T getVal() {
	return data;
    }

    public synchronized void setVal(T newVal) {
	data = newVal;
    }
}
