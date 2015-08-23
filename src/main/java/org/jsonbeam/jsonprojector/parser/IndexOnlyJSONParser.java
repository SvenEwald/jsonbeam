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

import java.util.Optional;
import java.util.function.Supplier;

import org.jsonbeam.exceptions.ParseErrorException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.index.JBResultCollector;
import org.jsonbeam.index.JBSubQueries;
import org.jsonbeam.index.keys.ArrayIndexKey;
import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.io.CharacterSource;

public class IndexOnlyJSONParser extends JSONParser {

	public IndexOnlyJSONParser(final CharacterSource json, final JBResultCollector resultCollector) {
		super(json, resultCollector);
	}

	public void createIndex() {
		expectMoreData();
		currentChar = json.nextConsumingWhitespace();
		expect(currentChar, "{[");
		if (currentChar == '{') {
			createIndex(ElementKey.ROOT);
			return;
		}
		else if (currentChar == '[') {
			createIndex(new ArrayIndexKey(0));
			return;
		}
		throw new ParseErrorException(json.getPosition(), "{[", currentChar);
	}

	public void createIndex(ElementKey currentKey) {
		assert resultCollector != null;
		nextChar: while (json.hasNext()) {
			char c = json.nextConsumingWhitespace();
			sameChar: while (true) {
				if (c == '{') { // begin of object
					if (ElementKey.ROOT != currentKey) {
						resultCollector.pushPath(currentKey);
						foundObjectPath(currentKey, () -> new ObjectReference());
						c = consumeAfterValue(currentKey);
						continue sameChar;
					}
					continue nextChar;
				}
				if (c == '}') { // end of object
					if (resultCollector.isPathEmpty()) {
						return;
					}
					currentKey = resultCollector.popPath();
					c = consumeAfterValue(currentKey);
					continue sameChar;
				}
				// if (char[i] == '\\') {
				// ++i; // TODO: mark current value as quoted
				// continue;
				// }
				if (c == '[') {
					if (currentKey != ElementKey.ROOT) {
						resultCollector.pushPath(currentKey);

					}
					currentKey = new ArrayIndexKey(0);
					continue nextChar;
				}
				if (c == ']') {
					currentKey = resultCollector.popPath();
					c = consumeAfterValue(currentKey);
					continue sameChar;
				}
				// parseValue
				if (!(currentKey instanceof ArrayIndexKey)) {
					//if (currentRef.peek() instanceof ObjectReference) {
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

	protected void foundObjectPath(final ElementKey currentKey, final Supplier<ObjectReference> objRef) {
		Optional<JBSubQueries> subCol = resultCollector.foundObjectPath(objRef);
		if (subCol.isPresent()) {
			JBSubQueries subQueries = subCol.get();
			new IndexOnlyJSONParser(json, subQueries).createIndex(ElementKey.ROOT);
			//reference.addSubCollector(subQueries);
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
