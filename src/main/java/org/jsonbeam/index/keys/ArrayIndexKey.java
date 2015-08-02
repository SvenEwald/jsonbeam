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
