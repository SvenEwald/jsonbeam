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

public interface Example4 {

	@JBRead("[0]")
	Example4 getFirstEntry();

	@JBRead("[*]")
	List<String> getStrings();

	// Projecions to arrays not supported yet
	//	@JBExpect({ "xxx", "yyy" })
	//	default List<String> getArrayInArray() {
	//		Example4 firstEntry = getFirstEntry();
	//		Assert.assertNotNull(firstEntry);
	//		Example4 secondEntry = firstEntry.getFirstEntry();
	//		Assert.assertNotNull(secondEntry);
	//		return secondEntry.getStrings();
	//	}

	@JBRead("[0].[0].[0]")
	@JBExpect(strings = "xxx")
	String getFirstString();
}
