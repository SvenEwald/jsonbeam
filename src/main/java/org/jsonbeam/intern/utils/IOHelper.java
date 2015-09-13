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
package org.jsonbeam.intern.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * @author Sven
 */
public class IOHelper {

	public static final Charset UTF_BOM = new Charset("UTF_BOM", null) {

		@Override
		public boolean contains(Charset cs) {
			return false;
		}

		@Override
		public CharsetDecoder newDecoder() {
			throw new IllegalStateException();
		}

		@Override
		public CharsetEncoder newEncoder() {
			throw new IllegalStateException();
		}};
	
	private static final int BUFFER_SIZE = 0x2000;

	public static <T> T toBuffer(final IOSupplier<InputStream, IOException> iss, final BiFunction<byte[], Integer, T> client) {
		return toBuffer(iss.get(), client);
	}

	/**
	 * @param is
	 * @return
	 */
	public static <T> T toBuffer(final InputStream is, final BiFunction<byte[], Integer, T> client) {
		try (InputStream closeme = is) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			for (int len; (len = is.read(buffer)) != -1;) {
				baos.write(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			return client.apply(buffer, buffer.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Determine UTF character set via byte order mark
	 * 
	 * @param buffer
	 * @return UTF_16BE,UTF_16LE or UTF_8 if BOM is found, else nothing
	 */@Deprecated
	public static Optional<Charset> charSetForBOM(byte[] buffer) {
		if (buffer.length < 2) {
			return Optional.empty();
		}
		if ((buffer[0] == (byte) 0xFE) && (buffer[1] == (byte) 0xFF)) {
			return Optional.of(StandardCharsets.UTF_16BE);
		}
		if ((buffer[0] == (byte) 0xFF) && (buffer[1] == (byte) 0xFE)) {
			return Optional.of(StandardCharsets.UTF_16LE);
		}
		if (buffer.length < 3) {
			return Optional.empty();
		}
		if ((buffer[0] == (byte) 0xEF) && (buffer[1] == (byte) 0xBB) && (buffer[2] == (byte) 0xBF)) {
			return Optional.of(StandardCharsets.UTF_8);
		}
		return Optional.empty();
	}
	
	/**
	 * Determine UTF character set via byte order mark
	 * 
	 * @param buffer
	 * @return UTF_16BE,UTF_16LE or UTF_8 if BOM is found, else nothing
	 */
	public static Optional<String> charSetNameForBOM(byte[] buffer,int offset) {
		if (buffer.length < 2+offset) {
			return Optional.empty();
		}
		int o=offset;
		if ((buffer[o] == (byte) 0xFE) && (buffer[o+1] == (byte) 0xFF)) {
			return Optional.of("UTF-16BE");
		}
		if ((buffer[o] == (byte) 0xFF) && (buffer[o+1] == (byte) 0xFE)) {
			return Optional.of("UTF-16LE");
		}
		if (buffer.length < 3+offset) {
			return Optional.empty();
		}
		if ((buffer[o] == (byte) 0xEF) && (buffer[o+1] == (byte) 0xBB) && (buffer[o+2] == (byte) 0xBF)) {
			return Optional.of("UTF-8");
		}
		return Optional.empty();
	}
	
}
