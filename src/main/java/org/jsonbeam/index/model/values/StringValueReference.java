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
import org.jsonbeam.io.CharacterSource;

public final class StringValueReference implements Reference {

	private final int length;
	private final int start;
	private final CharacterSource source;

	public StringValueReference(final int start, final int length, final CharacterSource source) {
		this.start = start;
		this.length = length;
		this.source = source;
	}

	@Override
	public String apply() {
		StringBuilder builder = new StringBuilder();
		CharacterSource source2 = source.getSourceFromPosition(start);
		for (int i = 0; i < length; ++i) {
			builder.append(source2.getNext());
		}
		return builder.toString();//source.getSourceFromPosition(start).asCharSequence(length).toString();
		//return new StringBuilder(source.getSequence(start, end)).toString();
	}

	@Override
	public String toString() {
		return apply();
	}
}
