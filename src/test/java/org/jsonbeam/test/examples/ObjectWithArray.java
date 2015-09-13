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
package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface ObjectWithArray {

	static String JSON = "{\r\n" + //
			"	array1:[a,b],\r\n" + //
			"	array2:[{},{}],\r\n" + //
			"	object1:{ a:1,b:null,c:true,}\r\n" + //
			"	emptyArray:[],\r\n" + //
			"	emptyObject:{}\r\n" + //
			"}";

	// @JBExpect({ "a", "b" })
	// @JBRead("$.array1[*]")
	// List<String> getArray2();

	@JBExpect(strings = { "a", "b" })
	@JBRead("$.array1[*]")
	List<String> getArray1();

	@JBExpect(strings = { "a" })
	@JBRead("$.array1[0]")
	List<String> getArray1A();

	@JBExpect(strings = "a")
	@JBRead("$.array1[0]")
	String getArray1Aa();

	@JBExpect(strings = { "b" })
	@JBRead("$.array1[1]")
	List<String> getArray1B();

	@JBExpect(strings = "b")
	@JBRead("$.array1[1]")
	String getArray1Ba();

	@JBExpect()
	@JBRead("$.emptyArray")
	List<String> getEmptyArray();

}
