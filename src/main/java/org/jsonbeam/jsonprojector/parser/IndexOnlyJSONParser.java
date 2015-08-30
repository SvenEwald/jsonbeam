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

import java.util.Deque;
import java.util.Optional;
import java.util.function.Supplier;

import org.jsonbeam.exceptions.ParseErrorException;
import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.index.JBResultCollector;
import org.jsonbeam.index.JBSubQueries;
import org.jsonbeam.index.keys.ArrayIndexKey;
import org.jsonbeam.index.keys.ElementKey;
import org.jsonbeam.index.model.IndexReference;
import org.jsonbeam.index.model.ObjectReference;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.index.model.values.StringValueReference;
import org.jsonbeam.io.CharacterSource;

public class IndexOnlyJSONParser extends JSONParser {

	public IndexOnlyJSONParser(final CharacterSource json, final JBResultCollector resultCollector) {
		super(json, resultCollector);
	}

	public IndexReference createIndex() {
		currentChar = json.nextConsumingWhitespace();
		expect(currentChar, "{[");
		if (currentChar == '{') {
			createIndex(ElementKey.ROOT, null);
			return null;
		}
		else if (currentChar == '[') {
			createIndex(new ArrayIndexKey(0), null);
			return null;
		}
		throw new ParseErrorException(json.getPosition(), "{[", currentChar);
	}

	public IndexReference createIndex(ElementKey currentKey, Deque<Reference> notUsed) {
		assert resultCollector != null;
		nextChar: while (json.hasNext()) {
			char c = json.nextConsumingWhitespace();
			sameChar: while (true) {
				if (c == '{') { // begin of object
					if (ElementKey.ROOT != currentKey) {
						resultCollector.pushPath(currentKey);
						currentKey = foundObjectPath(null, currentKey, ObjectReference::new);
						c = consumeAfterValue(currentKey);
						continue sameChar;
					}
					continue nextChar;
				}
				if (c == '}') { // end of object
					if (resultCollector.isPathEmpty()) {
						return null;
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
					if (resultCollector.isPathEmpty()) {
						return null;
					}
					currentKey = resultCollector.popPath();
					c = consumeAfterValue(currentKey);
					continue sameChar;
				}
				// parseValue
				if (!(currentKey instanceof ArrayIndexKey)) {
					if (c == '"') {
						currentKey = json.parseJSONKey();
						c = json.nextConsumingWhitespace();
						if (c != ':') {
							throw new ParseErrorException(json.getPosition(), ':', c);
						}
					}
					else {
						currentKey = parseUnquotedJSONKey(c);
					}
					c = json.nextConsumingWhitespace();
				}

				if ((c == '[') || (c == '{')) {
					continue sameChar;
				}
				if (!resultCollector.currentKeyMightBeInterresting(currentKey)) {
					if (c == '"') {
						json.skipToQuote();
						c=json.nextConsumingWhitespace();
					}
					else {
						long result = json.skipToStringEnd();
						c = (char) (result & 0xffff);
						if (c <= ' ') {
							c = json.nextConsumingWhitespace();
						}
					}
					if (',' == c) {
						currentKey.next();
						continue nextChar;
					}
					if (c <= ' ') {
						c = json.nextConsumingWhitespace();
					}
					continue sameChar;
				}
				Reference valueRef;
				switch (c) {
				case '"':
					valueRef = parseJSONString();
					c = json.nextConsumingWhitespace();
					break;
				case 'n': {
					int start = json.getPosition();
					long result = json.findNull();
					int length = (int) (result >> 16);
					valueRef = length == -1 ? Reference.NULL : new StringValueReference(start, length + 1, json);
					c = (char) (result & 0xffff);
				}
					break;
				case 't': {
					int start = json.getPosition();
					long result = json.findTrue();
					int length = (int) (result >> 16);
					valueRef = length == -1 ? Reference.TRUE : new StringValueReference(start, length + 1, json);
					c = (char) (result & 0xffff);
				}
					break;
				case 'f': {
					int start = json.getPosition();
					long result = json.findFalse();
					int length = (int) (result >> 16);
					valueRef = length == -1 ? Reference.FALSE : new StringValueReference(start, length + 1, json);
					c = (char) (result & 0xffff);
				}
					break;
				default: { //parse unquoted string
					int start = json.getPosition();
					long result = json.skipToStringEnd();
					int length = (int) (result >> 16);
					c = (char) (result & 0xffff);
					valueRef = new StringValueReference(start, length + 1, json);
				}
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

}
