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

import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.index.keys.ElementKey;
import org.jsonbeam.intern.index.keys.KeyReference;

/**
 * @author Sven
 */
public interface CharacterSource {

	boolean hasNext();

	char getNext();

	int getPosition();

	default char nextConsumingWhitespace() {
		char c;
		while (hasNext()) {
			c = getNext();
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
			c = getNext();
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

	/**
	 * Convert this CharacterSource to a String
	 * @param length
	 * @return
	 */
	default CharSequence asCharSequence(int length){
		final StringBuilder builder = new StringBuilder();
		for (int i=0;i<length;++i) {
			builder.append(getNext());
		}
		return builder;
	}
	
	default Closeable ioHandle() {		
		return ()->{};
	}

	default KeyReference parseJSONKey() {
		int start = getPosition();
		int hash = 0;
		int length = 0;
		while (hasNext()) {
			char c = getNext();
			if (c == '"') {
				return new KeyReference(start + 1, length, hash, this);
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
			c = getNext();
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
		char c=getNext();
		if (c!='u') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='l') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='l') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
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
		char c=getNext();
		if (c!='r') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='u') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='e') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
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
		char c=getNext();
		if (c!='a') {
			return skipToStringEnd()+(2<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='l') {
			return skipToStringEnd()+(3<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='s') {
			return skipToStringEnd()+(4<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
		if (c!='e') {
			return skipToStringEnd()+(5<<16);
		}
		if (!hasNext()) {
			throw new  UnexpectedEOF(getPosition());
		}
		c=getNext();
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
			chars[i]=source.getNext();
		}
		key.setChars(chars,0);
	}

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
