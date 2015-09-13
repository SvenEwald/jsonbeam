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
			throw new JBIOException("Illegal quote '{0}' at position {1}", "\\" + c2, getPosition());
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

	//	char decodeISO88591() {
	//		// ISO8859-1 shares 256 codepoints with UTF16
	//		return (char) (getNextByte() & 0xff);
	//	}

	//	char decodeCP1252() {
	//		int a = getNextByte() & 0xFF;
	//		char c = CP1252.toUTF16[a];
	//		return c;
	//	}

	//	char decodeUTF16BE() {
	//		int a = getNextByte() & 0xff;
	//		int b = getNextByte() & 0xff;
	//		return (char) ((a << 8) | b);
	//	}
	//
	//	char decodeUTF16LE() {
	//		int b = getNextByte() & 0xff;
	//		int a = getNextByte() & 0xff;
	//		return (char) ((a << 8) | b);
	//	}

	//	/**
	//	 * Decode UTF-8 sequences on the fly. Needed because the character source needs to track the current position of the buffer.
	//	 *
	//	 * @return
	//	 */
	//	char decodeUTF8() {
	//		int first = getNextByte();
	//		if ((first & 0b10000000) == 0) { // 7 Bits
	//			return (char) first;
	//		}
	//
	//		if ((first & 0b11000000) == 0b10000000) { // in surrogate sequence
	//			if (prevThirdByte == 0) {
	//				throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	//			}
	//			int fourth = first;//it was the fourth char we just read
	//			char c = (char) (((0b11011100 | ((prevThirdByte & 0b00001100) >> 2)) << 8) | ((prevThirdByte & 0b00000011) << 6) | (fourth & 0b00111111));
	//			prevThirdByte = 0;
	//			return c;
	//		}
	//		if (prevThirdByte != 0) {
	//			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	//		}
	//
	//		int second = getNextByte();
	//		if ((second & 0b11000000) != 0b10000000) {
	//			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	//		}
	//		if ((first & 0b11100000) == 0b11000000) { // 11 Bits
	//			return (char) (((first & 0b00011111) << 6) | (second & 0b00111111));
	//		}
	//		int third = getNextByte();
	//		if ((third & 0b11000000) != 0b10000000) {
	//			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	//		}
	//		if ((first & 0b11110000) == 0b11100000) { // 16 Bits
	//			return (char) (((first & 0b00011111) << 12) | ((second & 0b00111111) << 6) | (third & 0b00111111));
	//		}
	//		// code points producing a surrogate
	//		if ((first & 0b11111000) == 0b11110000) { // 21 Bits
	//			prevThirdByte = third;
	//			int highSurrogate = (((0b11011000 | (first & 0b00000111)) << 8) | ((second & 0b00111111) << 2) | ((third & 0b00110000) >> 4));
	//			//Integer.toHexString(highSurrogate)
	//			return (char) (highSurrogate - 0x40);
	//		}
	//		throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	//	}

	//	/**
	//	 * @return the charset
	//	 */
	//	public Charset getCharset() {
	//		return charset;
	//	}
}
