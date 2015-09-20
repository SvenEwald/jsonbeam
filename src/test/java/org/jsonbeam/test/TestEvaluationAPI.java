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

import static org.junit.Assert.*;

import java.util.Scanner;

import org.jsonbeam.JsonProjector;
import org.junit.Test;

/**
 * @author Sven
 */
public class TestEvaluationAPI {

	private final String json = new Scanner(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/jsonbeam/test/examples/Small.json")).useDelimiter("\\A").next();

	@Test
	public void testAsString() {
		assertEquals("on\toff",new JsonProjector().onJSONString(json).evalJPath("debug").asString().get());
	}
	
	@Test
	public void testAsString2() {
		assertEquals("on\toff",new JsonProjector().onJSONString(json).evalJPath("debug").as(String.class).get());
	}
	
	@Test
	public void testAsStringStream() {
		assertEquals("on\toff",new JsonProjector().onJSONString(json).evalJPath("debug").asStreamOf(String.class).findFirst().get());
	}
	
	@Test
	public void testAsBoolean() {
		assertTrue(new JsonProjector().onJSONString(json).evalJPath("somebool").asBoolean().get().booleanValue());
		assertFalse(new JsonProjector().onJSONString(json).evalJPath("otherbool").asBoolean().get().booleanValue());
	}
	
	@Test
	public void testAsInt() {
		assertEquals(1,new JsonProjector().onJSONString(json).evalJPath("num").asInt().getAsInt());
	}
	
	@Test
	public void testAsLong() {
		assertEquals(1,new JsonProjector().onJSONString(json).evalJPath("num").asLong().getAsLong());
	}
}
