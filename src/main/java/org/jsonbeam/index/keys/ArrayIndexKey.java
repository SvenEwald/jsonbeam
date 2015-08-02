package org.jsonbeam.index.keys;



public class ArrayIndexKey implements ElementKey {
	private int index;

	public ArrayIndexKey(int pos) {
		this.index = pos;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayIndexKey other = (ArrayIndexKey) obj;
		if (index != other.index)
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		return result;
	}

	@Override
	public boolean matches(ElementKey otherKey) {
		if (!(otherKey instanceof ArrayIndexKey)) {
			return false;
		}
		return ((ArrayIndexKey) otherKey).index == index;
	}

	@Override
	public void next() {
		++index;
	}

	@Override
	public String toString() {
		return "[" + index + "]";
	}
}
