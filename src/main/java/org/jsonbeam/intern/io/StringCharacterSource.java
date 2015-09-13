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

import org.jsonbeam.intern.index.keys.KeyReference;

/**
 * @author Sven
 */
public class StringCharacterSource extends JsonCharacterSource {

	private final String buffer;

	/**
	 * @param json
	 */
	public StringCharacterSource(final String json,int position,int max) {
		super(position,max);
		this.buffer = json;
	}

	/**
	 * @param json
	 */
	public StringCharacterSource(String json) {
		this(json,-1,json.length()-1);
	}

	public String debug() {
		String debugString = "#####" + buffer + "#####";
		int debugCursor = 6 + cursor;
		return debugString.substring(debugCursor - 5, debugCursor) + ">" + debugString.charAt(debugCursor) + "<" + debugString.substring(debugCursor + 1, debugCursor + 4);
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
	public int getPrevPosition() {
		return cursor - 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int pos) {
		StringCharacterSource source = new StringCharacterSource(buffer,pos,max);
		return source;
	}

	public void setCharsBuffer(KeyReference key) {
		int start = key.getStart() + 1;
//		if (buffer instanceof String) {
//			String string = (String) buffer;
//			key.setChars(string.substring(start, start + key.length()).toCharArray(), start);
//			return;
//		}
		key.setChars(buffer.subSequence(start, start+key.length()).toString().toCharArray(),0);
	}


}
