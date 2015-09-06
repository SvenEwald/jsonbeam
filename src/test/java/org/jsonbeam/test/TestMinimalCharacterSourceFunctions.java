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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jsonbeam.intern.io.EncodedCharacterSource;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.CharsCharacterSource;
import org.jsonbeam.intern.io.FileCharacterSourceFactory;
import org.jsonbeam.intern.io.StringCharacterSource;
import org.jsonbeam.intern.utils.Pair;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Sven
 */
@RunWith(Parameterized.class)
public class TestMinimalCharacterSourceFunctions {

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
		String string = "1234";
		ByteArrayInputStream bais = new ByteArrayInputStream(string.getBytes(Charset.forName("CP1252")));

		Stream<Pair<Supplier<CharacterSource>, String>> stream = Stream.<Pair<Supplier<CharacterSource>, String>>of(//
				new Pair<>(() -> new StringCharacterSource(string), "StringCharacterSource"),//
				new Pair<>(() -> new CharsCharacterSource(string.toCharArray(),-1,string.length()-1), "CharsCharacterSource"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(Charset.forName("CP1252")),0, string.getBytes(Charset.forName("CP1252")).length, Charset.forName("CP1252")), "BytesCharacterSource_CP1252"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.US_ASCII),0, string.getBytes(StandardCharsets.US_ASCII).length, StandardCharsets.US_ASCII), "BytesCharacterSource_US_ASCII"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.ISO_8859_1),0, string.getBytes(StandardCharsets.ISO_8859_1).length, StandardCharsets.ISO_8859_1), "BytesCharacterSource_ISO_8859_1"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.UTF_8),0, string.getBytes(StandardCharsets.UTF_8).length, StandardCharsets.UTF_8), "BytesCharacterSource_UTF_8"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.UTF_16),0, string.getBytes(StandardCharsets.UTF_16).length, StandardCharsets.UTF_16), "BytesCharacterSource_UTF_16"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.UTF_16BE),0, string.getBytes(StandardCharsets.UTF_16BE).length, StandardCharsets.UTF_16BE), "BytesCharacterSource_UTF_16BE"), //
				new Pair<>(() -> EncodedCharacterSource.handleBOM(string.getBytes(StandardCharsets.UTF_16LE),0, string.getBytes(StandardCharsets.UTF_16LE).length, StandardCharsets.UTF_16LE), "BytesCharacterSource_UTF_16LE") //				
			//	new Pair<>(() -> createFile(string, Charset.forName("CP1252")), "FileCharacterSource_CP1252")//
		);
			return ()->stream.map(p->p.<Object[]>reduce((k,v)->new Object[]{k,v})).iterator();
	}

	private static CharacterSource createFile(String content, Charset charset) {
		File newFile;
		try {
			assertTrue(folder.getRoot().exists());
			newFile = folder.newFile("jb" + System.nanoTime() + "." + charset.name());
			try (FileOutputStream os = new FileOutputStream(newFile)) {
				os.write(content.getBytes(charset));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return  FileCharacterSourceFactory.sourceFor(newFile, charset);
	}

	@Test
	public void testPrimaryFunctions() {
		assertTrue(source.hasNext());
		assertEquals('1', source.next());
		assertTrue(source.hasNext());
		assertEquals('2', source.next());
		assertTrue(source.hasNext());
		assertEquals('3', source.next());
		assertTrue(source.hasNext());
		assertEquals('4', source.next());
		assertFalse(source.hasNext());
	}

	@Test
	public void testSub() {
		source.next();
		CharacterSource subsource = source.getSourceFromPosition(source.getPosition());
		assertTrue(subsource.hasNext());
		assertEquals("" + '2', "" + subsource.next());
		assertTrue(subsource.hasNext());
		assertEquals("" + '3', "" + subsource.next());
		assertTrue(subsource.hasNext());
		assertEquals("" + '4', "" + subsource.next());
		assertFalse(subsource.hasNext());
	}

	@Test
	public void testSubCharSeq() {
		//		assertEquals("123", source.asCharSequence(3).toString());
		//		CharacterSource subsource = source.getSourceFromPosition(1);
		//		assertEquals("234", subsource.asCharSequence(3).toString());
	}
}
