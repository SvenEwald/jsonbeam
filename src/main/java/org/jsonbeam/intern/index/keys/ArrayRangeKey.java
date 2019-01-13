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
package org.jsonbeam.intern.index.keys;

import java.util.stream.Collectors;

/**
 * @author sven
 */
public class ArrayRangeKey extends ArrayIndexKey implements MultipleMatchKey {

	private int end;
	private int step;

	/**
	 * @param pos
	 */
	public ArrayRangeKey(int pos, int end, int step) {
		super(pos);
		this.end = end;
		this.step = step;
	}

	@Override
	public boolean matches(ElementKey otherKey) {
		if (!(otherKey instanceof ArrayIndexKey)) {
			return false;
		}
		assert !(otherKey instanceof MultipleMatchKey) : "How did you end up matching two ranges??";
		int otherIndex = ((ArrayIndexKey) otherKey).index;
		return (otherIndex >= index) && (otherIndex <= end) && ((otherIndex - index) % step == 0);
	}

	@Override
	public String toString() {
		return "[" + index + ":" + end + ":" + step + "]";
	}

}
