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
package org.jsonbeam.intern.parser;

import java.util.Deque;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.jsonbeam.exceptions.ParseErrorException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.index.JBQueries;
import org.jsonbeam.intern.index.JBResultCollector;
import org.jsonbeam.intern.index.JBSubQueries;
import org.jsonbeam.intern.index.keys.ElementKey;
import org.jsonbeam.intern.index.keys.KeyReference;
import org.jsonbeam.intern.index.model.IndexReference;
import org.jsonbeam.intern.index.model.ObjectReference;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.index.model.values.StringValueReference;
import org.jsonbeam.intern.io.CharacterSource;

public abstract class JSONParser {

	//final public static BiFunction<CharacterSource, JBResultCollector, JSONParser> fModelParser=IterativeJSONParser::new;
	final public static BiFunction<CharacterSource, JBResultCollector, JSONParser> fIndexParser=IndexOnlyJSONParser::new;
	
	
	public static BiFunction<CharacterSource, JBResultCollector, JSONParser> fMethod=fIndexParser;

	private final static char[] TRUE = new char[] { 't', 'r', 'u', 'e' };

	private final static char[] FALS = new char[] { 'f', 'a', 'l', 's' };
	private final static char[] NULL = new char[] { 'n', 'u', 'l', 'l' };
	private static boolean isStringEnd(final char c) {
		return (c <= ' ') || (c == ',') || (c == ']') || (c == '}');
	}
	protected final CharacterSource json;
	protected final JBResultCollector resultCollector;
	protected char currentChar;

	protected JSONParser(final CharacterSource json, final JBResultCollector resultCollector) {
		Objects.requireNonNull(json);
		Objects.requireNonNull(resultCollector);
		this.json = json;
		this.resultCollector = (resultCollector);
	}

	protected char consumeAfterValue(final ElementKey currentKey) {
		char c = json.nextConsumingWhitespace();
		if (c == ',') {
			currentKey.next();
			c = json.next();
			if (c <= ' ') {
				c = json.nextConsumingWhitespace();
			}
			return c;
		}
		return c;

	}

	/**
	 * @return
	 */
	public abstract IndexReference createIndex();

	protected abstract IndexReference createIndex(ElementKey currentKey, final Deque<Reference> currentRef);

	protected void expect(final char c, final String expectedChars) {
		if (-1 == expectedChars.indexOf(c)) {
			throw new ParseErrorException(json.getPosition(), c, expectedChars);//FIXME
		}
	}

	protected void expectMoreData() {
		if (!json.hasNext()) {
			throw new UnexpectedEOF(json.getPosition());
		}
	}

	protected ElementKey foundObjectPath(final Deque<Reference> currentRef,final ElementKey currentKey, final Supplier<ObjectReference> objRef) {
//		if (!resultCollector.currentKeyMightBeInterresting(currentKey)) {
//			return ElementKey.INOBJECT;
//		}
		JBQueries subCol = resultCollector.foundObjectPath(objRef);
		if (subCol!=null) {
			 JSONParser.fMethod.apply(json, subCol).createIndex(ElementKey.ROOT,null);
			ElementKey popPath = resultCollector.popPath();
			return popPath;
		}
		return ElementKey.INOBJECT;
	}

//	protected KeyReference parseJSONKey() {
//		int start = json.getPosition();
//		int hash = 0;
//		int length = 0;
//		while (json.hasNext()) {
//			char c = json.getNext();
//			if (c == '"') {
//				return new KeyReference(start + 1, length, hash, json);
//			}
//			++length;
//			hash = (31 * hash) + c;
//		}
//		throw new UnexpectedEOF(json.getPosition());
//	}

//	protected Reference parseJSONString() {
//		int start = json.getPosition();
//		int length = 0;
//		while (json.hasNext()) {
//			char c = json.getNext();
//			if (c=='"') {
//				return new StringValueReference(start + 1, length, json);
//			}
//			++length;
//		}
//		throw new UnexpectedEOF(json.getPosition());
//	}
	
	protected Reference parseJSONString() {
		int start=json.getPosition();
		int length=json.skipToQuote();
		return new StringValueReference(start, length, json);
	}
	
	protected KeyReference parseUnquotedJSONKey(final char firstChar) {
		int start = json.getPrevPosition();
		int hash = firstChar;
		int length = 1;
		while (json.hasNext()) {
			char c = json.next();
			//if (c == '"') {
			if (':' == c) { //FIXME: handle"\:"
				//cursor = i + 1;
				return new KeyReference(start, length, hash, json);
			}
			++length;
			hash = (31 * hash) + c;
		}
		throw new UnexpectedEOF(json.getPosition());
	}

	protected Reference parseUnquotedJSONString(char c) {
		int start = json.getPosition();
		int tc = 4, tf = 4, tn = 4;
		int length = 0;
		//		for (int i = 0; (i < 4) && json.hasNext(); ++i) {
		int i = 0;
		do {
			//			char c = json.getNext();
			//			if (isStringEnd(c)) {
			//				return new StringValueReference(start, json.getPosition() - start, json);
			//			}
			if (TRUE[i] == c) {
				--tc;
			}
			else if (FALS[i] == c) {
				--tf;
				if (i == 2) {
					--tn;
				}
			}
			else if (NULL[i] == c) {
				--tn;
			}
			else {
				if (isStringEnd(c)) {
					currentChar = c;
					return new StringValueReference(start, length, json);
				}
				break;
			}
			expectMoreData();
			c = json.next();
			++length;
			++i;
		} while (i < 4);

		if ((i == 4)) {
			if (isStringEnd(c)) {
				currentChar = c;
				if (tc == 0) {
					return Reference.TRUE;
				}
				if (tn == 0) {
					return Reference.NULL;
				}
			}
			if ((tf == 0) && (c == 'e')) {
				c = json.next();
				if (isStringEnd(c)) {
					currentChar = c;
					return Reference.FALSE;
				}
				++length;
			}
		}

		while (json.hasNext()) {
			c = json.next();
			++length;
			if (isStringEnd(c)) {
				currentChar = c;
				return new StringValueReference(start, length, json);
			}

			//			if (c == '\\') {
			//				StringBuilder builder = new StringBuilder();
			//				builder.append(array, start, i - start);
			//				for (int j = i; j < max; ++j) {
			//					c = array.charAt(j);
			//					if (isStringEnd(c)) {
			//						//cursor = j + 1;
			//						return new StringCopyReference(builder);
			//					}
			//					if (c == '\\') {
			//						switch (c = array.charAt(++j)) {
			//						case '"':
			//						case '\\':
			//						case ',':
			//						case ']':
			//						case '}':
			//						case '/':
			//							builder.append(c);
			//							continue;
			//						case 'n':
			//							builder.append('\n');
			//							continue;
			//						case 'r':
			//							builder.append('\r');
			//							continue;
			//						case 't':
			//							builder.append('\t');
			//							continue;
			//						case 'b':
			//							builder.append('\b');
			//							continue;
			//						case 'f':
			//							builder.append('\f');
			//							continue;
			//						case 'u':
			//							builder.append(Character.toChars(Integer.valueOf(new StringBuilder(array.subSequence(i, i + 4)).toString(), 16)));
			//							j += 4;
			//							continue;
			//
			//						default:
			//						}
			//						continue;
			//					}
			//					builder.append(c);
			//				}
			//			}
		}
		throw new UnexpectedEOF(json.getPosition());
	}
}
