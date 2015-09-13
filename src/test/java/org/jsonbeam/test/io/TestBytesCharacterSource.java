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
package org.jsonbeam.test.io;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import org.jsonbeam.intern.io.EncodedCharacterSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sven
 */
public class TestBytesCharacterSource {

	@Test
	public void testTrivialUTF8Sequences() {
		check("", StandardCharsets.UTF_8);
		check("ßäöÄÖpüp", StandardCharsets.UTF_8);
		check("ßäöÄÖpüp", StandardCharsets.UTF_16BE);
		check("ßäöÄÖpüp", StandardCharsets.UTF_16LE);
	}

	@Test
	public void testUnicodeBMPSpace() {
		StringBuilder builder = new StringBuilder();
		IntStream.range(0, 0xD7FF).filter(c -> c != '\\').forEach(builder::appendCodePoint);
		//Skip D800 -DFFF surrogate range
		IntStream.range(0xE000, 0xFFFF).forEach(builder::appendCodePoint);
		check(builder.toString(), StandardCharsets.UTF_8);

		check(builder.toString(), StandardCharsets.UTF_16BE);

		check(builder.toString(), StandardCharsets.UTF_16LE);
	}

	@Test
	public void testUnicodeSurrogates() {
		StringBuilder builder = new StringBuilder();
		IntStream.range(0x10000, 0x110000).forEach(builder::appendCodePoint);
		check(builder.toString(), StandardCharsets.UTF_8);

		check(builder.toString(), StandardCharsets.UTF_16BE);

		check(builder.toString(), StandardCharsets.UTF_16LE);
	}

	@Test
	public void testSISO_8859_1() {
		StringBuilder builder = new StringBuilder();
		IntStream.range(0, 0x100).filter(c -> c != '\\').forEach(builder::appendCodePoint);
		check(builder.toString(), StandardCharsets.ISO_8859_1);
	}

	@Test
	public void testCP1252() {
		//StringBuilder builder = new StringBuilder();
		//IntStream.range(0, 0x100).filter(c -> c != '\\').filter(c -> CP1252.toUTF16[c] != 0xFFFF).forEach(builder::appendCodePoint);
		//IntStream.range(0, 0x100).filter(c -> c != '\\').forEach(builder::appendCodePoint);
		byte[] bytes = new byte[0x100];
		IntStream.range(0, 0x100).filter(c -> c != '\\').forEach(i -> bytes[i] = (byte) i);
		//IntStream.range(0, 0x100).forEach(i -> bytes[i] = (byte) i);
		String string = new String(bytes, Charset.forName("cp1252"));
		//string.chars().peek(System.out::print).map(i -> (char) i).mapToObj(c -> Integer.toHexString(c)).forEach(s -> System.out.println("0x" + s + ","));

		EncodedCharacterSource source =  EncodedCharacterSource.handleBOM(bytes,0, bytes.length, Charset.forName("cp1252"));
		StringBuilder stringBuilder = new StringBuilder(string.length());
		for (int i=0;i<string.length();++i) {
			stringBuilder.append(source.next());
		}
		String decodedString = stringBuilder.toString();//source.asCharSequence(string.length()).toString();
		assertEquals(string, decodedString);
	}

	private void check(final String string, final Charset charset) {
		byte[] bytes = string.getBytes(charset);
		//		for (byte b : bytes) {
		//			System.out.println("0x" + Integer.toHexString(b));
		//		}
		EncodedCharacterSource source =  EncodedCharacterSource.handleBOM(bytes,0, bytes.length, charset);
		StringBuilder stringBuilder = new StringBuilder(string.length());
		for (int i=0;i<string.length();++i) {
			stringBuilder.append(source.next());
		}
		String decodedString = stringBuilder.toString();//source.asCharSequence(string.length()).toString();
		//		System.out.println(Integer.toHexString(string.charAt(0)) + Integer.toHexString(string.charAt(1)));
		//		System.out.print(Integer.toBinaryString(string.charAt(0)));
		//		System.out.print(" ");
		//		System.out.println(Integer.toBinaryString(string.charAt(1)));
		//
		//		System.out.println(Integer.toHexString(decodedString.charAt(0)) + Integer.toHexString(decodedString.charAt(1)));
		//		System.out.print(Integer.toBinaryString(decodedString.charAt(0)));
		//		System.out.print(" ");
		//		System.out.println(Integer.toBinaryString(decodedString.charAt(1)));
		assertEquals(string.length(), decodedString.length());
		for (int i = 0; i < string.length(); ++i) {
			assertEquals("at pos " + i, Integer.toHexString(string.charAt(i)), Integer.toHexString(decodedString.charAt(i)));
		}
		assertEquals(string, decodedString);
	}
}
