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

import java.util.function.Function;
import java.util.stream.Stream;

import org.jsonbeam.intern.index.keys.KeyReference;
import org.jsonbeam.intern.io.CharSeqCharacterSource;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.CharsCharacterSource;
import org.jsonbeam.intern.io.EncodedCharacterSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sven
 */
@RunWith(Parameterized.class)
public class TestCharacterSourceParsing {

	private final Function<String, CharacterSource> sourceSup;

	public TestCharacterSourceParsing(Function<String, CharacterSource> sourceSup, String s) {
		this.sourceSup = sourceSup;
	}

	@Parameters(name = "{1}")
	public static Iterable<Object[]> params() {
		return () -> Stream.of(
				new Object[] { (Function<String, CharacterSource>) s -> new CharsCharacterSource(s.toCharArray(), -1, s.toCharArray().length - 1), "CharsCharacterSource" },//
				new Object[] { (Function<String, CharacterSource>) s -> new CharSeqCharacterSource(s), "CharSeqCharacterSource" },//
				new Object[] { (Function<String, CharacterSource>) s -> EncodedCharacterSource.handleBOM(s.getBytes(UTF_16), 0, s.getBytes(UTF_16).length, UTF_16), "UTF_16" },
				new Object[] { (Function<String, CharacterSource>) s -> EncodedCharacterSource.handleBOM(s.getBytes(UTF_16BE), 0, s.getBytes(UTF_16BE).length, UTF_16BE), "UTF_16BE" },
				new Object[] { (Function<String, CharacterSource>) s -> EncodedCharacterSource.handleBOM(s.getBytes(UTF_16LE), 0, s.getBytes(UTF_16LE).length, UTF_16LE), "UTF_16LE" },
				new Object[] { (Function<String, CharacterSource>) s -> EncodedCharacterSource.handleBOM(s.getBytes(UTF_8), 0, s.getBytes(UTF_8).length, UTF_8), "UTF_8" }).iterator();
	}

	@Test
	public void testNext() {
		CharacterSource source = sourceSup.apply("Häáu");
		assertTrue(source.hasNext());
		assertEquals("H", "" + source.next());
		assertTrue(source.hasNext());
		assertEquals("ä", "" + source.next());
		assertTrue(source.hasNext());
		assertEquals("á", "" + source.next());
		assertTrue(source.hasNext());
		assertEquals("u", "" + source.next());
		assertFalse(source.hasNext());
	}

	@Test
	public void testConsumeWhitespace() {
		CharacterSource source = sourceSup.apply("a \t b");
		source.next();
		assertEquals("b", "" + source.nextConsumingWhitespace());
		assertFalse(source.hasNext());
	}

	@Test
	public void testConsumeWhitespace2() {
		CharacterSource source = sourceSup.apply("á \n b");
		source.next();
		assertEquals("b", "" + source.nextConsumingWhitespace());
		assertFalse(source.hasNext());
	}

	@Test
	public void testSkipToQuote() {
		CharacterSource source = sourceSup.apply("abcá\"qsdf");
		source.next();
		assertEquals(3, source.skipToQuote());
		assertEquals("q", "" + source.next());
	}

	@Test
	public void testSkipToStringEnd() {
		CharacterSource source = sourceSup.apply("abcasf ");
		long r = source.skipToStringEnd();
		assertEquals(6,r>>16);
		assertEquals(" ",""+Character.valueOf((char) (r&0xffff)));
	}
	
	@Test
	public void testSkipToStringEnd2() {
		CharacterSource source = sourceSup.apply("abcafá ");
		long r = source.skipToStringEnd();
		assertEquals(6,r>>16);
		assertEquals(" ",""+Character.valueOf((char) (r&0xffff)));
	}
	
	@Test
	public void testSkipToStringEnd3() {
		CharacterSource source = sourceSup.apply("abcafá}");
		long r = source.skipToStringEnd();
		assertEquals(6,r>>16);
		assertEquals("}",""+Character.valueOf((char) (r&0xffff)));
	}
	
	@Test
	public void testSkipToStringEnd4() {
		CharacterSource source = sourceSup.apply("abcafá,");
		long r = source.skipToStringEnd();
		assertEquals(6,r>>16);
		assertEquals(",",""+Character.valueOf((char) (r&0xffff)));
	}
	
	@Test
	public void testParseJsonKey() {
		CharacterSource source = sourceSup.apply("abcafá\"x");
		KeyReference key = source.parseJSONKey();
		assertEquals("x",""+source.next());
		assertEquals("abcafá",key.toString());
	}
}
