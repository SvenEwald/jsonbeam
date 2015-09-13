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
package org.jsonbeam.intern.utils.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;

public final class ObjectStack<T> implements Iterable<T> {

	@SuppressWarnings("unchecked")
	private T[] values = (T[]) new Object[100];

	private int pos = -1;

	public ObjectStack() {
	}

	//	public ObjectStack(final ObjectStack<T> src) {
	//		this.values = src.values.clone();
	//		this.pos = src.pos;
	//	}

	public void addAll(final ObjectStack<T> elements) {
		elements.forEach(this::push);
	}

	//	public boolean contains(final T e) {
	//		Objects.requireNonNull(e);
	//		for (T t : this) {
	//			if (t.equals(e)) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	public Iterator<T> descendingIterator() {
		return new Iterator<T>() {
			private int pos = size() - 1;

			@Override
			public boolean hasNext() {
				return pos >= 0;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new IndexOutOfBoundsException();
				}
				return values[pos--];
			}
		};
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof ObjectStack)) {
			return false;
		}
		@SuppressWarnings("unchecked")
		ObjectStack<T> o = (ObjectStack<T>) obj;
		if (size() != o.size()) {
			return false;
		}
		for (int i = 0; i < size(); ++i) {
			if (!values[i].equals(o.values[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		if (size() == 0) {
			return 0;
		}
		int hash = 0;
		for (T t : this) {
			hash = (31 * hash) + t.hashCode();
		}
		return hash;
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private int pos = 0;

			@Override
			public boolean hasNext() {
				return pos < size();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new IndexOutOfBoundsException();
				}
				return values[pos++];
			}
		};
	}

	public T peek() {
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		return values[pos];
	}

	public T pop() {
		if (pos < 0) {
			throw new IndexOutOfBoundsException();
		}
		return values[pos--];
	}

	public ObjectStack<T> push(final T value) {
		Objects.requireNonNull(value, "This class does not take null values.");
		if (++pos >= values.length) {
			values = Arrays.copyOf(values, values.length + 50);
		}
		values[pos] = value;
		return this;
	}

	public int size() {
		return pos + 1;
	}

	public Stream<T> stream() {
		return isEmpty() ? Stream.empty() : Arrays.stream(values).limit(size());
	}
}
