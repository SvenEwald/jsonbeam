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
package org.jsonbeam.test;

import org.jsonbeam.intern.index.JBQueries;
import org.jsonbeam.intern.index.JBResultCollector;
import org.jsonbeam.intern.io.CharSeqCharacterSource;
import org.jsonbeam.intern.parser.JSONParser;
import org.junit.Test;

/**
 * @author Sven
 */
public class TestMinimalParserFunctions {

	private final static String JSON1 = "{\"a\":\"b\"}";

	private final static String JSON2 = "{\"a\": {\"b\":\"c\"}}";

	private final static String JSON3 = "{\"a\": {}}";

	private final static String JSON4 = "{\"a\":\"b\",\"c\":\"d\"}";

	private final static String JSON5 = "{\"a\":[1,2,3]}";

	private final static String JSON6 = "{\n" + "    \"debug\": \"on\\toff\",\n" + "    \"num\" : 1\n" + "\n" + "}";

	private final static String JSON7 = "{\"a\":null, \"b\": true , \"c\": false }";

	private final static String JSON8 = "{\"a\": [ { b: null } ] }";

	private final static String JSON9 = "[\"a\",null]";

	private final static JBResultCollector EMPTY_COLLECTOR = new JBQueries();

	@Test
	public void testJSON1() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON1), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON2() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON2), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON3() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON3), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON4() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON4), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON5() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON5), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON6() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON6), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON7() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON7), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON8() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON8), EMPTY_COLLECTOR);
		parser.createIndex();
	}

	@Test
	public void testJSON9() {
		JSONParser parser = JSONParser.fMethod.apply(new CharSeqCharacterSource(JSON9), EMPTY_COLLECTOR);
		parser.createIndex();
	}
}
