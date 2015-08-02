package org.jsonbeam.test;

import org.jsonbeam.index.keys.ArrayIndexKey;
import org.jsonbeam.index.keys.KeyReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestKeyReference {

	@Test
	public void ensureEquals() {
		KeyReference key = new KeyReference(1, 1 + "huhu".length(), "huhu".hashCode(), "ahuhub");
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
