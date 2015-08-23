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
import java.util.function.Supplier;

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
import org.jsonbeam.io.CharacterSource;

public class IterativeJSONParser extends JSONParser {

	public IterativeJSONParser(final CharacterSource json, final JBResultCollector resultCollector) {
		super(json, resultCollector);
	}

	public IndexReference createIndex() {
		expectMoreData();
		currentChar = json.nextConsumingWhitespace();
		expect(currentChar, "{[");
		ArrayDeque<Reference> arrayDeque = new ArrayDeque<>(32);
		if (currentChar == '{') {
			ObjectReference objRef = new ObjectReference();
			arrayDeque.push(objRef);
			return createIndex(ElementKey.ROOT, arrayDeque);
		}
		else if (currentChar == '[') {
			ArrayReference ref = new ArrayReference();
			arrayDeque.push(ref);
			return createIndex(new ArrayIndexKey(0), arrayDeque);
		}
		throw new ParseErrorException(json.getPosition(), "{[", currentChar);
	}

	public IndexReference createIndex(ElementKey currentKey, final Deque<Reference> currentRef) {
		assert resultCollector != null;
		nextChar: while (json.hasNext()) {
			char c = json.nextConsumingWhitespace();
			sameChar: while (true) {
				if (c == '{') { // begin of object
					ObjectReference objRef = new ObjectReference();
					currentRef.push(objRef);
					if (ElementKey.ROOT != currentKey) {
						resultCollector.pushPath(currentKey);
						foundObjectPath(currentRef, currentKey, () -> objRef);
						c = consumeAfterValue(currentKey);
						continue sameChar;
					}
					continue nextChar;
				}
				if (c == '}') { // end of object
					if (currentRef.isEmpty()) {
						throw new ParseErrorException(json.getPosition(), "Unexpected object end at pos {0}");
					}
					if (!(currentRef.peek() instanceof ObjectReference)) {
						throw new ParseErrorException(json.getPosition(), "Unexcpected } at pos {0}");
					}
					Reference reference = currentRef.pop();
					if (resultCollector.isPathEmpty()) {
						if (!(reference instanceof IndexReference)) {
							throw new ParseErrorException(json.getPosition(), "Unexpected array content");
						}
						return (IndexReference) reference;//new ParseResult(reference, json.getPosition());
					}
					currentKey = resultCollector.popPath();
					currentRef.peek().addChild(currentKey, reference);
					//				if (!(currentRef instanceof ObjectReference)) {
					//					//FIXME: kann das überhaupt sein?
					//					currentKey = KeyReference.ARRAY_ENTRY;
					//				}
					c = consumeAfterValue(currentKey);
					continue sameChar;
				}
				// if (char[i] == '\\') {
				// ++i; // TODO: mark current value as quoted
				// continue;
				// }
				if (c == '[') {
					ArrayReference ref = new ArrayReference();
					if (currentKey != ElementKey.ROOT) {
						resultCollector.pushPath(currentKey);

					}
					currentRef.push(ref);
					currentKey = new ArrayIndexKey(0);
					continue nextChar;
				}
				if (c == ']') {
					if (currentRef.isEmpty()) {
						throw new ParseErrorException(json.getPosition(), "Unexpected array end");
					}
					Reference reference = currentRef.pop();
					if (currentRef.isEmpty()) {
						if (!(reference instanceof IndexReference)) {
							throw new ParseErrorException(json.getPosition(), "Unexpected array content");
						}
						return (IndexReference) reference;
					}
					currentKey = resultCollector.popPath();
					currentRef.peek().addChild(currentKey, reference);
					c = consumeAfterValue(currentKey);
					continue sameChar;
				}
				if (currentRef.isEmpty()) {
					throw new ParseErrorException(json.getPosition(), "Unexpected content at pos {0} after object or array");
				}
				// parseValue
				if (currentRef.peek() instanceof ObjectReference) {
					if (c == '"') {
						currentKey = parseJSONKey(ch -> ch == '"');
						expectMoreData();
						c = json.nextConsumingWhitespace();//consumeColon();
						if (c != ':') {
							throw new ParseErrorException(json.getPosition(), ':', c);
						}
					}
					else {
						currentKey = parseUnquotedJSONKey(c);// = parseJSONKey(ch -> ch == ':');

					}
					expectMoreData();
					c = json.nextConsumingWhitespace();
				}

				if ((c == '{') || (c == '[')) {
					continue sameChar;
				}
				Reference valueRef;
				if (c == '"') {
					valueRef = parseJSONString(ch -> ch == '"');
					expectMoreData();
					c = json.nextConsumingWhitespace();
				}
				else {
					valueRef = parseUnquotedJSONString(c);
					c = currentChar;

				}
				currentRef.peek().addChild(currentKey, valueRef);
				resultCollector.pushPath(currentKey);
				resultCollector.foundValuePath(valueRef);
				resultCollector.popPath();
				if (',' == c) {
					currentKey.next();
					continue nextChar;
				}
				if (c <= ' ') {
					c = json.nextConsumingWhitespace();
				}
				continue sameChar;
			}
		}
		throw new UnexpectedEOF(json.getPosition());
	}

	protected void foundObjectPath(final Deque<Reference> currentRef, final ElementKey currentKey, final Supplier<ObjectReference> objRef) {
		Optional<JBSubQueries> subCol = resultCollector.foundObjectPath(objRef);
		if (subCol.isPresent()) {
			JBSubQueries subQueries = subCol.get();
			IndexReference reference = new IterativeJSONParser(json, subQueries).createIndex(ElementKey.ROOT, currentRef);
			reference.addSubCollector(subQueries);
			ElementKey popPath = resultCollector.popPath();
			//Reference objOrArray = currentRef.pop();
			//						if (popPath instanceof ArrayIndexKey) {
			//							currentKey = ((ArrayIndexKey)popPath).next();
			//						}
			//	objRef.addChild(currentKey, result.getRootReference());//FIXME: Behandlung für array elemente fehlt
			//cursor = result.getEndPosition();

			//consumeAfterValue(currentChar, currentKey);
		}
	}
}
