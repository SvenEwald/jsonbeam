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
package org.jsonbeam.jsonprojector.parser;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import org.jsonbeam.exceptions.ParseErrorException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.index.JBResultCollector;
import org.jsonbeam.index.JBSubQueries;
import org.jsonbeam.index.keys.ArrayIndexKey;
import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.ArrayReference;
import org.jsonbeam.index.model.IndexReference;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;

public class IterativeJSONParser extends JSONParser {

	public IterativeJSONParser(final CharSequence json, final JBResultCollector resultCollector) {
		super(json, resultCollector);
	}

	private void consumeAfterValue(final ElementKey currentKey) {
		consumeWhitespace();
		if (cursor >= json.length()) {
			return;
		}
		if (json.charAt(cursor) == ',') {
			cursor++;
			currentKey.next();
		}
	}

	private void consumeColon() {
		consumeWhitespace();
		if (json.charAt(cursor) != ':') {
			throw new ParseErrorException(cursor, json, ":", json.charAt(cursor));
		}
		++cursor;
		consumeWhitespace();
	}

	public ParseResult createIndex() {
		consumeWhitespace();
		expect("{[");
		if (json.charAt(cursor) == '[') {

		}
		return createIndex(0, new ArrayDeque<>(32));
	}

	public ParseResult createIndex(final int start, final Deque<Reference> currentRef) {
		assert resultCollector != null;
		cursor = start;
		ElementKey currentKey = ElementKey.ROOT;
		while (cursor < json.length()) { // parse value

			char c = consumeWhitespace();
			if (c == '{') { // begin of object
				++cursor;
				ObjectReference objRef = new ObjectReference();
				currentRef.push(objRef);
				if (ElementKey.ROOT != currentKey) {
					resultCollector.pushPath(currentKey);
					foundObjectPath(currentRef, currentKey, objRef);
				}
				continue;
			}

			if (c == '}') { // end of object
				if (currentRef.isEmpty()) {
					throw new ParseErrorException(cursor, "Unexpected object end");
				}
				if (!(currentRef.peek() instanceof ObjectReference)) {
					throw new ParseErrorException(cursor, "Unexcpected }");
				}
				++cursor;
				Reference reference = currentRef.pop();
				if (resultCollector.isPathEmpty()) {
					return new ParseResult(reference, cursor);
				}
				currentKey = resultCollector.popPath();
				currentRef.peek().addChild(currentKey, reference);
				//				if (!(currentRef instanceof ObjectReference)) {
				//					//FIXME: kann das überhaupt sein?
				//					currentKey = KeyReference.ARRAY_ENTRY;
				//				}
				consumeAfterValue(currentKey);
				continue;
			}
			// if (char[i] == '\\') {
			// ++i; // TODO: mark current value as quoted
			// continue;
			// }
			if (c == '[') {
				++cursor;
				ArrayReference ref = new ArrayReference();
				if (currentKey != ElementKey.ROOT) {
					resultCollector.pushPath(currentKey);

				}
				currentRef.push(ref);
				currentKey = new ArrayIndexKey(0);
				continue;
			}
			if (c == ']') {
				if (currentRef.isEmpty()) {
					throw new ParseErrorException(cursor, "Unexpected array end");
				}
				++cursor;
				Reference reference = currentRef.pop();
				if (currentRef.isEmpty()) {
					return new ParseResult(reference, cursor);
				}
				currentKey = resultCollector.popPath();
				currentRef.peek().addChild(currentKey, reference);
				consumeAfterValue(currentKey);
				continue;
			}
			if (currentRef.isEmpty()) {
				throw new ParseErrorException(cursor, "Unexpected content after object or array");
			}
			// parseValue
			if (currentRef.peek() instanceof ObjectReference) {
				if (c == '"') {
					currentKey = parseJSONKey(json, cursor + 1, ch -> ch == '"');
					consumeColon();
				}
				else {
					currentKey = parseJSONKey(json, cursor, ch -> (ch == ':') || (ch <= ' '));
					if (json.charAt(cursor) <= ' ') {
						consumeColon();
					}
				}

			}
			consumeWhitespace();

			if ((json.charAt(cursor) == '{') || (json.charAt(cursor) == '[')) {
				continue;
			}
			Reference valueRef;
			if (json.charAt(cursor) == '"') {
				valueRef = parseJSONString(json, cursor + 1, ch -> ch == '"');
			}
			else {
				valueRef = parseUnquotedJSONString(json, cursor);
			}
			currentRef.peek().addChild(currentKey, valueRef);
			resultCollector.pushPath(currentKey);
			resultCollector.foundValuePath(valueRef);
			resultCollector.popPath();
			consumeAfterValue(currentKey);
		}
		throw new UnexpectedEOF(cursor, json);
	}

	private void foundObjectPath(final Deque<Reference> currentRef, final ElementKey currentKey, final ObjectReference objRef) {
		Optional<JBSubQueries> subCol = resultCollector.foundObjectPath(objRef);
		if (subCol.isPresent()) {
			JBSubQueries subQueries = subCol.get();
			ParseResult result = new IterativeJSONParser(json, subQueries).createIndex(cursor, currentRef);
			assert result.getRootReference() instanceof ObjectReference;
			((IndexReference) result.getRootReference()).addSubCollector(subQueries);
			ElementKey popPath = resultCollector.popPath();
			//Reference objOrArray = currentRef.pop();
			//						if (popPath instanceof ArrayIndexKey) {
			//							currentKey = ((ArrayIndexKey)popPath).next();
			//						}
			//	objRef.addChild(currentKey, result.getRootReference());//FIXME: Behandlung für array elemente fehlt
			cursor = result.getEndPosition();

			consumeAfterValue(currentKey);
		}
	}

	private void expect(final String expectedChars) {
		if (-1 == expectedChars.indexOf(json.charAt(cursor))) {
			throw new ParseErrorException(cursor, "", expectedChars, json.charAt(cursor));//FIXME
		}
	}

}
