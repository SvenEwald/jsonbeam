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

import java.io.Closeable;
import java.nio.charset.Charset;

import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.index.keys.ElementKey;
import org.jsonbeam.intern.index.keys.KeyReference;

/**
 * @author Sven
 */
public interface CharacterSource {

	boolean hasNext();

	char next();

	int getPosition();

	default char nextConsumingWhitespace() {
		char c;
		while (hasNext()) {
			c = next();
			if (c > ' ') {
				return c;
			}
		}
		throw new UnexpectedEOF(getPosition());
	}
	
	default int skipToQuote() {
		char c;
		int length=0;
		while (hasNext()) {
			c = next();
			if (c == '"') {
				return length;
			}
			++length;
		}
		throw new UnexpectedEOF(getPosition());
	}

	/**
	 * @param a
	 * @return
	 */
	CharacterSource getSourceFromPosition(int a);
	
	default Closeable ioHandle() {		
		return ()->{};
	}

	default KeyReference parseJSONKey() {
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
	default long skipToStringEnd() {
		char c;
		int length=0;
		while (hasNext()) {
			c = next();
			if  ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
				long result=length;
				return result<<16|c;
			}
			++length;
		}
		throw new UnexpectedEOF(getPosition());
	}

	/**
	 * @return
	 */
	default long findNull() {
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		char c=next();
		if (c!='u') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='l') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='l') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if  ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) { 
			return -1l<<16|c;
				
		}
		return skipToStringEnd()+(5<<16);
	}
	
	/**
	 * @return
	 */
	default long findTrue() {
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		char c=next();
		if (c!='r') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='u') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='e') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if  ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) { 
			return -1l<<16|c;
				
		}
		return skipToStringEnd()+(5<<16);
	}
	
	/**
	 * @return
	 */
	default long findFalse() {
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		char c=next();
		if (c!='a') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='l') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='s') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if (c!='e') {
			return skipToStringEnd()+(5<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=next();
		if  ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) { 
			return -1l<<16|c;
				
		}
		return skipToStringEnd()+(6<<16);
	}
	
	default void setCharsBuffer(KeyReference key) {
		int length =key.length();
		int start=key.getStart();
		char[] chars=new char[length];
		CharacterSource source = getSourceFromPosition(start);
		for (int i=0;i<length;++i) {
			chars[i]=source.next();
		}
		key.setChars(chars,0);
	}

	/**
	 * @return
	 */
	Charset getCharset();

	/**
	 * @return
	 */
	int getPrevPosition();

//	/**
//	 * @param start
//	 * @param length
//	 * @return
//	 */
//	default char[] getCharsFrom(int start, int length) {
//		char[] chars=new char[length];
//		CharacterSource source = getSourceFromPosition(start);
//		for (int i=0;i<length;++i) {
//			chars[i]=source.getNext();
//		}
//		return chars;
//	}
	
}
