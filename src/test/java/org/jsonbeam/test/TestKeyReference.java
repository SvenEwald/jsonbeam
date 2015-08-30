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

import org.jsonbeam.intern.index.keys.ArrayIndexKey;
import org.jsonbeam.intern.index.keys.KeyReference;
import org.jsonbeam.intern.io.StringCharacterSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestKeyReference {

	@Test
	public void ensureEquals() {
		KeyReference key = new KeyReference(1, "huhu".length(), "huhu".hashCode(), new StringCharacterSource("ahuhub"));
		assertEquals(key, new KeyReference("huhu"));
	}

	@Test
	public void ensureHashCode() {
		assertEquals(new KeyReference("huhu").hashCode(), "huhu".hashCode());
	}

	@Test
	public void testArrayIndexKey() {
		ArrayIndexKey fourth = new ArrayIndexKey(3);
		ArrayIndexKey first = new ArrayIndexKey(0);
		assertFalse(fourth.equals(first));
		assertFalse(first.equals(fourth));
		first.next();
		first.next();
		first.next();
		assertTrue(fourth.equals(first));
		assertTrue(first.equals(fourth));
	}
}
