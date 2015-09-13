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

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.CharsCharacterSource;
import org.jsonbeam.intern.io.CharSeqCharacterSource;
import org.jsonbeam.intern.utils.Pair;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.nio.charset.StandardCharsets.UTF_16;
import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.nio.charset.StandardCharsets.UTF_16LE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.jsonbeam.intern.io.EncodedCharacterSource.handleBOM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sven
 */
@RunWith(Parameterized.class)
public class TestMinimalCharacterSourceFunctions {

	private final static Charset CP1252 = Charset.forName("CP1252");

	@ClassRule
	public static TemporaryFolder folder = new TemporaryFolder();

	private final Supplier<CharacterSource> sourceSup;
	private CharacterSource source;

	public TestMinimalCharacterSourceFunctions(Supplier<CharacterSource> sourceSup, String testname) {
		this.sourceSup = sourceSup;
	}

	@Before
	public void initSource() throws IOException {
		source = sourceSup.get();
	}

	@Parameters(name = "{1}")
	public static Iterable<Object[]> params() throws Exception {
		String string = "1234\" \tX";
		//	ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes(CP1252));

		Stream<Pair<Supplier<CharacterSource>, String>> stream = Stream.<Pair<Supplier<CharacterSource>, String>> of(//
				new Pair<>(() -> new CharSeqCharacterSource(string), "StringCharacterSource"), //
				new Pair<>(() -> new CharsCharacterSource(string.toCharArray(), -1, string.length() - 1), "CharsCharacterSource"), //

		new Pair<>(() -> handleBOM(string.getBytes(CP1252), 0, string.getBytes(CP1252).length, CP1252), "BytesCharacterSource_CP1252"), //
				new Pair<>(() -> handleBOM(string.getBytes(US_ASCII), 0, string.getBytes(US_ASCII).length, US_ASCII), "BytesCharacterSource_US_ASCII"), //
				new Pair<>(() -> handleBOM(string.getBytes(ISO_8859_1), 0, string.getBytes(ISO_8859_1).length, ISO_8859_1), "BytesCharacterSource_ISO_8859_1"), //
				new Pair<>(() -> handleBOM(string.getBytes(UTF_8), 0, string.getBytes(UTF_8).length, UTF_8), "BytesCharacterSource_UTF_8"), //
				new Pair<>(() -> handleBOM(string.getBytes(UTF_16), 0, string.getBytes(UTF_16).length, UTF_16), "BytesCharacterSource_UTF_16"), //
				new Pair<>(() -> handleBOM(string.getBytes(UTF_16BE), 0, string.getBytes(UTF_16BE).length, UTF_16BE), "BytesCharacterSource_UTF_16BE"), //
				new Pair<>(() -> handleBOM(string.getBytes(UTF_16LE), 0, string.getBytes(UTF_16LE).length, UTF_16LE), "BytesCharacterSource_UTF_16LE") //				

		//	new Pair<>(() -> createFile(string, Charset.forName("CP1252")), "FileCharacterSource_CP1252")//
		);
		return () -> stream.map(p -> p.<Object[]> reduce((k, v) -> new Object[] { k, v })).iterator();
	}

	@Test
	public void testPrimaryFunctions() {
		Stream.of('1', '2', '3', '4', '"', ' ', '\t', 'X').forEachOrdered(c -> {
			assertTrue(source.hasNext());
			assertEquals((char) c, source.next());
		});

		assertFalse(source.hasNext());
	}

	@Test
	public void testSub() {
		source.next();
		CharacterSource subsource = source.getSourceFromPosition(source.getPosition());
		Stream.of('2', '3', '4', '"', ' ', '\t', 'X').forEachOrdered(c -> {
			assertTrue(subsource.hasNext());
			assertEquals((char) c, subsource.next());
		});

		assertFalse(subsource.hasNext());
	}

	@Test
	public void testSkipToQuoteAndWhitespace() {
		int skipToQuote = source.skipToQuote();
		assertEquals(4, skipToQuote);
		char c = source.nextConsumingWhitespace();
		assertEquals('X', c);
	}
}
