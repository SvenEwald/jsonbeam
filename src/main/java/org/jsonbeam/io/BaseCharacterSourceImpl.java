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
package org.jsonbeam.io;

import java.util.Locale;
import java.util.function.IntSupplier;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.jsonbeam.exceptions.JBIOException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.utils.CP1252;

/**
 * @author sven
 */
abstract class BaseCharacterSourceImpl implements CharacterSource {

	protected int initValue = -1;
	protected int cursor = -1;
	protected int max;
	//storing state to decode UTF8 sequences that produce multiple chars
	private int prevThirdByte = 0;

	protected final IntSupplier nextChar;
	protected final Charset charset;

	protected BaseCharacterSourceImpl(final Charset charset, final int initialValue, final int max, final IntSupplier nextChar) {
		this.charset = charset;
		this.initValue = initialValue;
		this.cursor = initialValue;
		this.max = max;
		this.nextChar = nextChar;
	}

	protected BaseCharacterSourceImpl(final Charset charset) {
		this.charset = charset;
		if (charset == null) {
			nextChar = this::getNextByte;
		}
		else if (charset.equals(StandardCharsets.ISO_8859_1)) {
			nextChar = this::decodeISO88591;
		}
		else if (charset.equals(StandardCharsets.US_ASCII)) {
			nextChar = this::getNextByte;
		}
		else if (charset.equals(StandardCharsets.UTF_8)) {
			nextChar = this::decodeUTF8;
		}
		else if (charset.equals(StandardCharsets.UTF_16BE)) {
			nextChar = this::decodeUTF16BE;
		}
		else if (charset.equals(StandardCharsets.UTF_16LE)) {
			nextChar = this::decodeUTF16LE;
		}
		else if (charset.equals(StandardCharsets.UTF_16)) {
			// see http://unicode.org/faq/utf_bom.html
			// if there is no BOM, it should be BE
			nextChar = this::decodeUTF16BE;
		}
		else if (charset.name().equalsIgnoreCase("cp1252")) {
			nextChar = this::decodeCP1252;
		}
		else if (charset.name().equalsIgnoreCase("windows-1252")) {
			nextChar = this::decodeCP1252;
		}
		else {
			throw new JBIOException("Charset '{0}' is currently not supported.", charset.displayName(Locale.US));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char getNext() {
		int c = nextChar.getAsInt();
		if ('\\' == c) {
			if (!hasNext()) {
				throw new UnexpectedEOF(cursor);
			}
			int c2 = nextChar.getAsInt();
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
				StringBuilder hexnumber = new StringBuilder();
				if (!hasNext()) {
					throw new UnexpectedEOF(cursor);
				}
				hexnumber.append((char) nextChar.getAsInt());
				if (!hasNext()) {
					throw new UnexpectedEOF(cursor);
				}
				hexnumber.append((char) nextChar.getAsInt());
				if (!hasNext()) {
					throw new UnexpectedEOF(cursor);
				}
				hexnumber.append((char) nextChar.getAsInt());
				if (!hasNext()) {
					throw new UnexpectedEOF(cursor);
				}
				hexnumber.append((char) nextChar.getAsInt());
				char cp = (char) Integer.parseInt(hexnumber.toString(), 16);
				return cp;
			default:
			}

		}
		return (char) c;
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

	abstract protected int getNextByte();

	char decodeISO88591() {
		// ISO8859-1 shares 256 codepoints with UTF16
		return (char) (getNextByte() & 0xff);
	}

	char decodeCP1252() {
		int a = getNextByte() & 0xFF;
		char c = CP1252.toUTF16[a];
		return c;
	}

	char decodeUTF16BE() {
		int a = getNextByte() & 0xff;
		int b = getNextByte() & 0xff;
		return (char) ((a << 8) | b);
	}

	char decodeUTF16LE() {
		int b = getNextByte() & 0xff;
		int a = getNextByte() & 0xff;
		return (char) ((a << 8) | b);
	}

	/**
	 * Decode UTF-8 sequences on the fly. Needed because the character source needs to track the current position of the buffer.
	 *
	 * @return
	 */
	char decodeUTF8() {
		int first = getNextByte();
		if ((first & 0b10000000) == 0) { // 7 Bits
			return (char) first;
		}

		if ((first & 0b11000000) == 0b10000000) { // in surrogate sequence
			if (prevThirdByte == 0) {
				throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
			}
			int fourth = first;//it was the fourth char we just read
			char c = (char) (((0b11011100 | ((prevThirdByte & 0b00001100) >> 2)) << 8) | ((prevThirdByte & 0b00000011) << 6) | (fourth & 0b00111111));
			prevThirdByte = 0;
			return c;
		}
		if (prevThirdByte != 0) {
			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
		}

		int second = getNextByte();
		if ((second & 0b11000000) != 0b10000000) {
			throw new JBIOException("Illegal UTF-8 sequence at pos {0}", cursor);
		}
		if ((first & 0b11100000) == 0b11000000) { // 11 Bits
			return (char) (((first & 0b00011111) << 6) | (second & 0b00111111));
		}
		int third = getNextByte();
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
}
