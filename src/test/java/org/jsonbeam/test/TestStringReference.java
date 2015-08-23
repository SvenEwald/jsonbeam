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

import org.jsonbeam.index.model.values.StringValueReference;
import org.jsonbeam.io.StringCharacterSource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Sven
 */
public class TestStringReference {

	@Test
	public void testStringRefFull() {
		StringValueReference reference = new StringValueReference(0, "huhu".length(), new StringCharacterSource("huhu"));
		assertEquals("huhu", reference.apply());
		assertEquals("huhu", reference.toString());
	}

	@Test
	public void testStringRefPart() {
		StringValueReference reference = new StringValueReference(1, "huhu".length(), new StringCharacterSource("shuhuv"));
		assertEquals("huhu", reference.apply());
		assertEquals("huhu", reference.toString());
	}

	@Test
	public void testSingleCharRef() {
		StringValueReference stringValueReference = new StringValueReference(2, 1, new StringCharacterSource("12345"));
		assertEquals("3", stringValueReference.apply());
		assertEquals("3", stringValueReference.toString());
	}
}
