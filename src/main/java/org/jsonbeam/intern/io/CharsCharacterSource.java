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
public class CharsCharacterSource extends JsonCharacterSource {

	final private char[] buffer;

	public CharsCharacterSource(char[] json,int pos,int max) {
		super(pos,max);
		this.buffer = json;
		assert max<json.length;
	}

//	public long findNull() {
//		if (cursor+4 >= max) {
//			throw new  UnexpectedEOF(getPosition());
//		}
//		int i=cursor;
//		char c=buffer[++i];
//		if (c!='u') {
//			return skipToStringEnd()+(2<<16);
//		}
//		c=buffer[++i];
//		if (c!='l') {
//			return skipToStringEnd()+(3<<16);
//		}
//		c=buffer[++i];
//		if (c!='l') {
//			return skipToStringEnd()+(4<<16);
//		}
//		c=buffer[++i];
//		cursor=i;
//		if  ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) { 
//			return -1l<<16|c;
//				
//		}
//		return skipToStringEnd()+(5<<16);
//	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNextByte() {
		return buffer[++cursor];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getPrevPosition() {
		return cursor-1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(int pos) {
		CharsCharacterSource source = new CharsCharacterSource(buffer,pos,max);
//		source.initValue = pos;
//		source.cursor = pos;
		return source;
	}

//	@Override
//	public char next() {
//		char c = buffer[++cursor];
//		if ('\\' == c) {
//			if (!hasNext()) {
//				throw new UnexpectedEOF(cursor);
//			}
//			int c2 = buffer[++cursor];
//			switch (c2) {
//			case '"':
//			case '\\':
//			case ',':
//			case ']':
//			case '}':
//			case '/':
//				return (char) c2;
//			case 'n':
//				return '\n';
//			case 'r':
//				return '\r';
//			case 't':
//				return '\t';
//			case 'b':
//				return '\b';
//			case 'f':
//				return '\f';
//			case 'u':
//				StringBuilder hexnumber = new StringBuilder();
//				if (!hasNext()) {
//					throw new UnexpectedEOF(cursor);
//				}
//				hexnumber.append((char) buffer[++cursor]);
//				if (!hasNext()) {
//					throw new UnexpectedEOF(cursor);
//				}
//				hexnumber.append((char) buffer[++cursor]);
//				if (!hasNext()) {
//					throw new UnexpectedEOF(cursor);
//				}
//				hexnumber.append((char) buffer[++cursor]);
//				if (!hasNext()) {
//					throw new UnexpectedEOF(cursor);
//				}
//				hexnumber.append((char) buffer[++cursor]);
//				char cp = (char) Integer.parseInt(hexnumber.toString(), 16);
//				return cp;
//			default:
//			}
//
//		}
//		return (char) c;
//	}

//	public char nextConsumingWhitespace() {
//		char c;
//		int m = max;
//		int i = cursor;
//		while (i < m) {
//			c = buffer[++i];
//			if (c > ' ') {
//				cursor = i;
//				return c;
//			}
//		}
//		throw new UnexpectedEOF(getPosition());
//	}
	
//	public KeyReference parseJSONKey() {
//		int start = cursor;
//		int i = start;
//		int m = max;
//		int hash = 0;
//		int length = 0;
//		nextChar: while (i < m) {
//			char c = buffer[++i];
//			sameChar: while (true) {
//				if (c == '"') {
//					cursor=i;
//					return new KeyReference(start, length, hash, this);
//				}
//				if ('\\' == c) {
//					if (i >= m) {
//						throw new UnexpectedEOF(i);
//					}
//					char c2 = buffer[++i];
//					if (c2 == 'u') {
//						StringBuilder hexnumber = new StringBuilder();
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						c = (char) Integer.parseInt(hexnumber.toString(), 16);
//						continue sameChar;
//					}
//					switch (c2) {
//					case '"':
//					case '\\':
//					case ',':
//					case ']':
//					case '}':
//					case '/':
//						c=c2;
//						break;
//					case 'n':
//						c= '\n';break;
//					case 'r':
//						c= '\r';break;
//					case 't':
//						c= '\t';break;
//					case 'b':
//						c= '\b';break;
//					case 'f':
//						c= '\f';break;
//					default:
//						throw new ParseErrorException(i, "Illegal escape char '"+c2+"'");
//					}
//					++length;
//					hash = (31 * hash) + c;
//					continue nextChar;
//				}	
//				++length;
//				hash = (31 * hash) + c;
//				continue nextChar;
//			}
//		}
//		throw new UnexpectedEOF(getPosition());
//	}
	
	public void setCharsBuffer(KeyReference key) {
		int start=key.getStart()+1;
		key.setChars(buffer, start);
	}
	
//	public int skipToQuote() {
//		char c;
//		int length = 0;
//		int m = max;
//		int i = cursor;
//		nextChar: while (i < m) {
//			c = buffer[++i];
//			sameChar: while (true) {
//				if ('"' == c) {
//					cursor = i;
//					return length;
//				}
//				if ('\\' == c) {
//					if (i >= m) {
//						throw new UnexpectedEOF(i);
//					}
//					char c2 = buffer[++i];
//					if (c2 == 'u') {
//						StringBuilder hexnumber = new StringBuilder();
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						c = (char) Integer.parseInt(hexnumber.toString(), 16);
//						continue sameChar;
//					}
//				}
//				++length;
//				continue nextChar;
//			}
//		}
//		throw new UnexpectedEOF(getPosition());
//	}
//
//	public long skipToStringEnd() {
//		char c;
//		int length = 0;
//		int m = max;
//		int i = cursor;
//		nextChar: while (i < m) {
//			c = buffer[++i];
//			sameChar: while (true) {
//				if ((c <= ' ') || (c == ',') || (c == ']') || (c == '}')) {
//					cursor = i;
//					long result=length;
//					return result<<16 | c;
//				}
//				if ('\\' == c) {
//					if (i >= m) {
//						throw new UnexpectedEOF(i);
//					}
//					char c2 = buffer[++i];
//					if (c2 == 'u') {
//						StringBuilder hexnumber = new StringBuilder();
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						if (i >= m) {
//							throw new UnexpectedEOF(i);
//						}
//						hexnumber.append((char) buffer[++i]);
//						c = (char) Integer.parseInt(hexnumber.toString(), 16);
//						continue sameChar;
//					}
//				}
//				++length;
//				continue nextChar;
//			}
//		}
//		throw new UnexpectedEOF(getPosition());
//	}
	
}
