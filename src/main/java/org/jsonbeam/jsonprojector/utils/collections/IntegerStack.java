package org.jsonbeam.jsonprojector.utils.collections;

import java.util.Arrays;

public class IntegerStack {

	private int[] values = new int[100];

	private int pos = -1;

	public IntegerStack() {
	}

	//	public IntegerStack(final IntegerStack src) {
	//		this.values = src.values.clone();
	//		this.pos = src.pos;
	//	}

	//	public int peek() {
	//		if (pos < 0) {
	//			throw new IndexOutOfBoundsException();
	//		}
	//		return values[pos];
	//	}

	public int pop() {
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		return values[pos--];
	}

	public IntegerStack push(final int value) {
		if (++pos >= values.length) {
			values = Arrays.copyOf(values, values.length + 50);
		}
		values[pos] = value;
		return this;
	}

	//	public int size() {
	//		return pos + 1;
	//	}
}
