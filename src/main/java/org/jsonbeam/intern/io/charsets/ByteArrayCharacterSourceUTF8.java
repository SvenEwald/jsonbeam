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
package org.jsonbeam.intern.io.charsets;

import org.jsonbeam.exceptions.JBIOException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.EncodedCharacterSource;

/**
 * @author sven
 */
public class ByteArrayCharacterSourceUTF8 extends EncodedCharacterSource {
	//storing state to decode UTF8 sequences that produce multiple chars
	private int prevThirdByte = 0;

	/**
	 * @param bytes
	 * @param offset
	 * @param length
	 */
	public ByteArrayCharacterSourceUTF8(byte[] bytes, int offset, int length) {
		super(bytes, offset, length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int pos) {
		ByteArrayCharacterSourceUTF8 source = new ByteArrayCharacterSourceUTF8(buffer, pos, max);
		return source;
	}

	private int readNextByte() {
		return buffer[++cursor];
	}

	@Override
	public int getNextByte() {
		int first = readNextByte();
		if ((first & 0b10000000) == 0) { // 7 Bits
			return (char) first;
		}
		return nextUTF8Byte(first);
	}

	public int nextUTF8Byte(int first) {
		//		int first = readNextByte();
		//		if ((first & 0b10000000) == 0) { // 7 Bits
		//			return (char) first;
		//		}

		if ((first & 0b11000000) == 0b10000000) { // in surrogate sequence
			if (prevThirdByte == 0) {
				throw new JBIOException("Illegal UTF-8 sequence at pos {0}", Integer.valueOf(cursor));
			}
			int fourth = first;//it was the fourth char we just read
			char c = (char) (((0b11011100 | ((prevThirdByte & 0b00001100) >> 2)) << 8) | ((prevThirdByte & 0b00000011) << 6) | (fourth & 0b00111111));
			prevThirdByte = 0;
			return c;
		}
		if (prevThirdByte != 0) {
			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
		}

		int second = readNextByte();
		if ((second & 0b11000000) != 0b10000000) {
			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
		}
		if ((first & 0b11100000) == 0b11000000) { // 11 Bits
			return (char) (((first & 0b00011111) << 6) | (second & 0b00111111));
		}
		int third = readNextByte();
		if ((third & 0b11000000) != 0b10000000) {
			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
		}
		if ((first & 0b11110000) == 0b11100000) { // 16 Bits
			return (char) (((first & 0b00011111) << 12) | ((second & 0b00111111) << 6) | (third & 0b00111111));
		}
		// code points producing a surrogate
		if ((first & 0b11111000) == 0b11110000) { // 21 Bits
			prevThirdByte = third;
			int highSurrogate = (((0b11011000 | (first & 0b00000111)) << 8) | ((second & 0b00111111) << 2) | ((third & 0b00110000) >> 4));
			//Integer.toHexString(highSurrogate)
			return (char) (highSurrogate - 0x40);
		}
		throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
	}

	@Override
	public int getPrevPosition() {
		// hack, this method is called for nonsurrogate chars only
		return cursor - 1;
	}

	public char nextConsumingWhitespace() {
		int c;
		int m = max;
		int i = cursor;
		while (i < m) {
			c = buffer[++i] & 0xff;
			if (c > ' ') {
				cursor = i;
				return (char) (((c & 0b10000000) == 0) ? c : nextUTF8Byte(c));
			}
		}
		throw new UnexpectedEOF(getPosition());
	}

	public int skipToQuote() {
		int c;
		int length = 0;
		int m = max;
		int i = cursor;
		while (i < m) {
			c = buffer[++i];
			if ('"' == c) {
				cursor = i;
				return length;
			}
			if ('\\' == c) {
				cursor = i;
				int r = unquotedNext();
				i = cursor;
				c = (char) (r & 0xffff);
				//length += 1 + r >> 16;
				++length;
				continue;
			}
			if ((c & 0b10000000) == 0b10000000) {
				cursor=i;
				nextUTF8Byte(c);	
				i=cursor;
			}

			++length;
		}
		throw new UnexpectedEOF(getPosition());
	}

	//	public KeyReference parseJSONKey() {
	//		int start = getPosition();
	//		int hash = 0;
	//		int length = 0;
	//		int i = start;
	//		int m = max;
	//		while (i < m) {
	//			int c = buffer[++cursor] & 0xff;
	//			if ((c & 0b10000000) == 0b10000000) {
	//				int a=cursor=i;
	//				c = nextUTF8Byte(c);
	//				i=cursor;
	//				length+=1+i-a;
	//			}
	//			if (c == '"') {
	//				cursor = i;
	//				return new KeyReference(start, length, hash, this);
	//			}
	//			if ('\\' == c) {
	//				cursor = i;
	//				int r = unquotedNext();
	//				i = cursor;
	//				c = (char) (r & 0xffff);
	//				length += r >> 16;
	//			}
	//			++length;
	//			hash = (31 * hash) + c;
	//		}
	//		throw new UnexpectedEOF(getPosition());
	//	}

}
