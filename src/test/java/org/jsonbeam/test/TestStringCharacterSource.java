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

import org.jsonbeam.io.CharacterSource;
import org.jsonbeam.io.StringCharacterSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Sven
 */
public class TestStringCharacterSource {

	@Test
	public void testPrimaryFunctions() {
		StringCharacterSource source = new StringCharacterSource("huhu");
		assertEquals(-1, source.getPosition());
		assertTrue(source.hasNext());
		assertEquals('h', source.getNext());
		assertTrue(source.hasNext());
		assertEquals('u', source.getNext());
		assertTrue(source.hasNext());
		assertEquals('h', source.getNext());
		assertTrue(source.hasNext());
		assertEquals('u', source.getNext());
		assertFalse(source.hasNext());
	}

	@Test
	public void testSub() {
		CharacterSource source = new StringCharacterSource("1234");
		source = source.getSourceFromPosition(1);
		assertEquals(0, source.getPosition());
		assertTrue(source.hasNext());
		assertEquals("" + '2', "" + source.getNext());
		assertTrue(source.hasNext());
		assertEquals("" + '3', "" + source.getNext());
		assertTrue(source.hasNext());
		assertEquals("" + '4', "" + source.getNext());
		assertFalse(source.hasNext());
	}

	@Test
	public void testSubCharSeq() {
		CharacterSource source = new StringCharacterSource("1234");
		assertEquals("123", source.asCharSequence(3));
		source = source.getSourceFromPosition(1);
		assertEquals("234", source.asCharSequence(3));
	}
}
