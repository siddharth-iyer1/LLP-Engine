package com.The.Boiz;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

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
