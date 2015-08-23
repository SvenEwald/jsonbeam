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
package org.jsonbeam.utils;

import java.util.function.BiFunction;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Sven
 */
public class IOHelper {

	private static final int FIRST_BUFFER_SIZE = 0x2000;

	public static <T> T toBuffer(final IOSupplier<InputStream, IOException> iss, final BiFunction<byte[], Integer, T> client) {
		return toBuffer(iss.get(), client);
	}

	/**
	 * @param is
	 * @return
	 */
	public static <T> T toBuffer(final InputStream is, final BiFunction<byte[], Integer, T> client) {
		try (InputStream closeme = is) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(FIRST_BUFFER_SIZE);
			byte[] buffer = new byte[FIRST_BUFFER_SIZE];
			for (int len; (len = is.read(buffer)) != -1;) {
				baos.write(buffer, 0, len);
			}
			buffer = baos.toByteArray();
			return client.apply(buffer, buffer.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
