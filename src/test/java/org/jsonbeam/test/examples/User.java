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

import java.util.stream.Stream;

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

/**
 * @author Sven
 *
 */
public interface User {

	@JBRead("..etag")
	@JBExpect(strings="\"TTbz3xy1I5OVJNV4ylIvI-QbXF4/UNCXwiESrbG7BQoWpEI399Fmn90\"")
	String getEtag();
	
	@JBRead("[0].etag")
	@JBExpect(strings="\"TTbz3xy1I5OVJNV4ylIvI-QbXF4/UNCXwiESrbG7BQoWpEI399Fmn90\"")
	String getEtag2();
	
	@JBRead("[0].locations[*].city")
	@JBExpect(strings={"Brooklyn","Niceville","Toronto"})
	Stream<String> getCities();
}
