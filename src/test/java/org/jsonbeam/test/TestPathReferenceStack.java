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

import org.jsonbeam.index.keys.ArrayIndexKey;
import org.jsonbeam.index.keys.KeyReference;
import org.jsonbeam.index.keys.PathReferenceStack;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPathReferenceStack {

	private static void ensureEquality(final Object a, final Object b) {
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void oneElement() {
		PathReferenceStack oneElement = new PathReferenceStack().push(new KeyReference("element"));
		PathReferenceStack oneElement2 = PathReferenceStack.parse("element");
		assertFalse(oneElement.isEmpty());
		assertFalse(oneElement2.isEmpty());
		ensureEquality(oneElement, oneElement2);
		//	assertTrue(oneElement.endsWith(oneElement2));
		assertTrue(oneElement.matches(oneElement2));
	}

	//	@Test
	//	public void twoElementsArray() {
	//		PathReferenceStack a = new PathReferenceStack().push(new KeyReference(".element")).push("[0]");
	//		PathReferenceStack b = PathReferenceStack.parse(".element[0]");
	//		assertFalse(a.isEmpty());
	//		assertFalse(b.isEmpty());
	//		ensureEquality(a, b);
	//	}

	@Test
	public void partialMatch() {
		PathReferenceStack a = PathReferenceStack.parse("$.menu.header");
		PathReferenceStack b = PathReferenceStack.parse("..header");

		assertTrue(b.matches(a));
		assertFalse(a.matches(b));
	}

	@Test
	public void partialMultiMatch() {
		PathReferenceStack a = PathReferenceStack.parse("$.menu.header");
		PathReferenceStack b = PathReferenceStack.parse("..menu..header");

		assertTrue(b.matches(a));
		assertFalse(a.matches(b));
	}

	@Test
	public void partialNonMatch() {
		PathReferenceStack a = PathReferenceStack.parse("$.menu.header");
		PathReferenceStack b = PathReferenceStack.parse("..fish..header");

		assertFalse(b.matches(a));
		assertFalse(a.matches(b));
	}

	@Test
	public void threeElements() {
		PathReferenceStack a = new PathReferenceStack().push(new KeyReference("menu")).push(new KeyReference("header"));
		PathReferenceStack b = PathReferenceStack.parse("$.menu.header");
		assertFalse(a.isEmpty());
		assertFalse(b.isEmpty());
		ensureEquality(a, b);
		//		assertTrue(a.endsWith(PathReferenceStack.parse(".header")));
		//		assertTrue(a.endsWith(PathReferenceStack.parse(".menu.header")));
		//		assertTrue(a.endsWith(b));
	}

	@Test
	public void twoElementsAttribute() {
		PathReferenceStack a = new PathReferenceStack().push(new KeyReference("element")).push(new KeyReference("attribute"));
		PathReferenceStack b = PathReferenceStack.parse(".element.attribute");
		assertFalse(a.isEmpty());
		assertFalse(b.isEmpty());
		ensureEquality(a, b);
		//		assertTrue(a.endsWith(PathReferenceStack.parse(".attribute")));
		//		assertTrue(b.endsWith(a));
		assertTrue(a.matches(b));
		assertTrue(b.matches(a));
	}

	@Test
	public void allArrayElements() {
		PathReferenceStack a = PathReferenceStack.parse("[*]");
		assertTrue(a.isPattern());
	}

	@Test
	public void certainArrayElement() {
		PathReferenceStack a = PathReferenceStack.parse("[14]");
		PathReferenceStack b = new PathReferenceStack().push(new ArrayIndexKey(14));
		assertFalse(a.isPattern());
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void testArrayReferenc() {
		PathReferenceStack path1 = new PathReferenceStack();
		PathReferenceStack path2 = new PathReferenceStack();
		ArrayIndexKey fourth = new ArrayIndexKey(3);
		ArrayIndexKey first = new ArrayIndexKey(0);
		path1.push(fourth);
		path2.push(first);
		assertFalse(path1.equals(path2));
		assertFalse(path2.equals(path1));
		path2.pop();
		first.next();
		first.next();
		first.next();
		path2.push(first);
		assertTrue(path1.equals(path2));
		assertTrue(path2.equals(path1));
		assertEquals(path1.hashCode(), path2.hashCode());
	}

	//	@Test
	//	public void wildcardMatch() {
	//		// new JSONPathParser("foo.*.bar").parse();
	//
	//		PathReferenceStack a = PathReferenceStack.parse(".foo.*.bar");
	//		PathReferenceStack b = new PathReferenceStack().push(new KeyReference(".foo")).push(new KeyReference.("?")).push(new KeyReference(".bar"));
	//		assertEquals(b, a);
	//		assertTrue(a.matches( PathReferenceStack.parse(".foo.something.bar")));
	//		assertFalse(a.matches( PathReferenceStack.parse(".foo.something.else.bar")));
	//	}
	//
	//	@Test
	//	public void allArrayElementsMatch() {
	//		PathReferenceStack a = PathReferenceStack.parse("$.menu.items[*].id");
	//		PathReferenceStack b = new PathReferenceStack().push("$").push(".menu").push(".items").push("[?]").push(".id");
	//		assertEquals(b, a);
	//		assertTrue(a.matches( PathReferenceStack.parse("$.menu.items[32].id")));
	//	}
}
