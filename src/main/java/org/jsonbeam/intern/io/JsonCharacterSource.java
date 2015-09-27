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

import org.jsonbeam.exceptions.JBIOException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.index.keys.KeyReference;

/**
 * @author sven
 */
abstract class JsonCharacterSource implements CharacterSource {

	final protected int initValue;
	protected int cursor;
	protected int max;

	protected JsonCharacterSource(final int initialIndex, final int maxIndex) {
		this.initValue = initialIndex;
		this.cursor = this.initValue;
		this.max = maxIndex;
		assert initialIndex <= maxIndex : initialIndex + "<=" + maxIndex;
	}

	protected JsonCharacterSource() {
		this.initValue = -1;
		this.cursor = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char next() {
		int c = getNextByte();
		if ('\\' == c) {
			return unquotedNext();
		}
		return (char) c;
	}

	private char unquotedNext() {
		if (!hasNext_()) {
			throw new UnexpectedEOF(cursor);
		}
		int c2 = getNextByte();
		switch (c2) {
		case '"':
		case '\\':
		case ',':
		case ']':
		case '}':
		case '/':
			return (char) c2;
		case 'n':
			return '\n';
		case 'r':
			return '\r';
		case 't':
			return '\t';
		case 'b':
			return '\b';
		case 'f':
			return '\f';
		case 'u':
			return unquoteHexCodePoint();
		default:
			throw new JBIOException("Illegal quote '{0}' at position {1}", "\\" + c2, Integer.valueOf(getPosition()));
		}
	}

	/**
	 * @return
	 */
	private char unquoteHexCodePoint() {
		StringBuilder hexnumber = new StringBuilder();
		if (!hasNext_()) {
			throw new UnexpectedEOF(cursor);
		}
		hexnumber.append((char) getNextByte());
		if (!hasNext_()) {
			throw new UnexpectedEOF(cursor);
		}
		hexnumber.append((char) getNextByte());
		if (!hasNext_()) {
			throw new UnexpectedEOF(cursor);
		}
		hexnumber.append((char) getNextByte());
		if (!hasNext_()) {
			throw new UnexpectedEOF(cursor);
		}
		hexnumber.append((char) getNextByte());
		char cp = (char) Integer.parseInt(hexnumber.toString(), 16);
		return cp;
	}

	@Override
	public int getPosition() {
		return cursor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return cursor < max;
	}

	/**
	 * Hack to define a static binding.
	 * 
	 * @return
	 */
	private boolean hasNext_() {
		return cursor < max;
	}

	abstract protected int getNextByte();

	public char nextConsumingWhitespace() {
		char c;
		while (hasNext()) {
			c = next();
			if (c > ' ') {
				return c;
			}
		}
		throw new UnexpectedEOF(getPosition());
	}

	public int skipToQuote() {
		char c;
		int length = 0;
		while (hasNext()) {
			c = next();
			if (c == '"') {
				return length;
			}
			++length;
		}
		throw new UnexpectedEOF(getPosition());
	}

	public KeyReference parseJSONKey() {
		int start = getPosition();
		int hash = 0;
		int length = 0;
		while (hasNext()) {
			char c = next();
			if (c == '"') {
				return new KeyReference(start, length, hash, this);
			}
			++length;
			hash = (31 * hash) + c;
		}
		throw new UnexpectedEOF(getPosition());
	}

	/**
	 * 
	 */
	public long skipToStringEnd() {
		char c;
		int length = 0;
		while (hasNext()) {
			c = next();
			if ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
				long result = length;
				return result << 16 | c;
			}
			++length;
		}
		throw new UnexpectedEOF(getPosition());
	}

	/**
	 * @return
	 */
	public long findNull() {
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		char c = next();
		if (c != 'u') {
			return skipToStringEnd() + (2 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'l') {
			return skipToStringEnd() + (3 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'l') {
			return skipToStringEnd() + (4 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
			return -1l << 16 | c;

		}
		return skipToStringEnd() + (5 << 16);
	}

	public long findTrue() {
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		char c = next();
		if (c != 'r') {
			return skipToStringEnd() + (2 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'u') {
			return skipToStringEnd() + (3 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'e') {
			return skipToStringEnd() + (4 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
			return -1l << 16 | c;

		}
		return skipToStringEnd() + (5 << 16);
	}

	public long findFalse() {
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		char c = next();
		if (c != 'a') {
			return skipToStringEnd() + (2 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'l') {
			return skipToStringEnd() + (3 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 's') {
			return skipToStringEnd() + (4 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if (c != 'e') {
			return skipToStringEnd() + (5 << 16);
		}
		if (!hasNext()) {
			throw new UnexpectedEOF(getPosition());
		}
		c = next();
		if ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
			return -1l << 16 | c;

		}
		return skipToStringEnd() + (6 << 16);
	}

	public void setCharsBuffer(KeyReference key) {
		int length = key.length();
		int start = key.getStart();
		char[] chars = new char[length];
		CharacterSource source = getSourceFromPosition(start);
		for (int i = 0; i < length; ++i) {
			chars[i] = source.next();
		}
		key.setChars(chars, 0);
	}
}
