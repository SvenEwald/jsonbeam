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
package org.jsonbeam.index.model.values;

import org.jsonbeam.index.model.Reference;

public final class StringValueReference implements Reference {

	private final int end;
	private final int start;

	public StringValueReference(final int start, final int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public String apply(final CharSequence array) {
		return new StringBuilder(array.subSequence(start, end)).toString();
		//return new String(array,start,end-start);
	}

}
