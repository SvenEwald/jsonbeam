/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
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
