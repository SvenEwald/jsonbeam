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
package org.jsonbeam.intern.io;

/**
 * @author Sven
 */
public class StringCharacterSource extends BaseCharacterSourceImpl {

	private final CharSequence buffer;

	/**
	 * @param json
	 */
	public StringCharacterSource(final CharSequence json) {
		super(null);
		this.buffer = json;
		max = buffer.length() - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNextByte() {
		return buffer.charAt(++cursor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int pos) {
		StringCharacterSource source = new StringCharacterSource(buffer);
		source.initValue = pos - 1;
		source.cursor = pos - 1;
		return source;
	}

	public String debug() {
		String debugString = "#####" + buffer + "#####";
		int debugCursor = 6 + cursor;
		return debugString.substring(debugCursor - 5, debugCursor) + ">" + debugString.charAt(debugCursor) + "<" + debugString.substring(debugCursor + 1, debugCursor + 4);
	}

	@Override
	public CharSequence asCharSequence(final int length) {
		return buffer.subSequence(initValue + 1, initValue + 1 + length);
	}

}
