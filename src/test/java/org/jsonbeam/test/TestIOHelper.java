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

import java.util.Random;

import java.io.ByteArrayInputStream;

import org.jsonbeam.intern.utils.IOHelper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author sven
 */
public class TestIOHelper {
	@Test
	public void testBufferFromInputstream() {
		testStreamToBufferFunction(0);
		testStreamToBufferFunction(1);
		testStreamToBufferFunction(10);
		{
			int size = 0x2000;
			testStreamToBufferFunction(size - 10);
			testStreamToBufferFunction(size - 1);
			testStreamToBufferFunction(size);
			testStreamToBufferFunction(size + 1);
			testStreamToBufferFunction(size + 10);
		}

		{
			int size = 0x2000 + (2 * 0x2000);
			testStreamToBufferFunction(size - 10);
			testStreamToBufferFunction(size - 1);
			testStreamToBufferFunction(size);
			testStreamToBufferFunction(size + 1);
			testStreamToBufferFunction(size + 10);
		}

		{
			int size = 0x2000 + (2 * 0x2000) + (4 * 0x2000);
			testStreamToBufferFunction(size - 10);
			testStreamToBufferFunction(size - 1);
			testStreamToBufferFunction(size);
			testStreamToBufferFunction(size + 1);
			testStreamToBufferFunction(size + 10);
		}
		{
			int size = 0x2000 + (2 * 0x2000) + (4 * 0x2000) + (8 * 0x2000);
			testStreamToBufferFunction(size - 10);
			testStreamToBufferFunction(size - 1);
			testStreamToBufferFunction(size);
			testStreamToBufferFunction(size + 1);
			testStreamToBufferFunction(size + 10);
		}

	}

	private void testStreamToBufferFunction(final int size) {
		byte[] origBuffer = new byte[size];
		new Random().nextBytes(origBuffer);
		IOHelper.toBuffer(new ByteArrayInputStream(origBuffer), (b, l) -> assertEquality(origBuffer, b, l));
	}

	/**
	 * @param origBuffer
	 * @param b
	 * @param l
	 * @return
	 */
	private Object assertEquality(final byte[] origBuffer, final byte[] b, final int l) {
		assertEquals(origBuffer.length, l);
		assertTrue(b.length >= l);
		for (int i = 0; i < l; ++i) {
			assertTrue("pos " + i, origBuffer[i] == b[i]);
		}
		return null;
	}
}
