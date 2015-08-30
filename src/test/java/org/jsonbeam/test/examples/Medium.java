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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Medium {

	@JBRead("web-app.servlet[*].servlet-name")
	Stream<String> getAllServletNamesAsStream();

	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	default List<String> testGetAllServletNamesAsStream() {
		return getAllServletNamesAsStream().collect(Collectors.toList());
	}

	@JBRead("web-app.servlet[*].servlet-name")
	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	List<String> getAllServletNamesAsList();

	@JBRead("web-app.servlet[*].servlet-name")
	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	String[] getAllServletNamesAsArray();

	@JBRead("web-app.servlet-mapping.cofaxCDS")
	@JBExpect(strings = "/")
	String getServletMapping();

	@JBRead("web-app.servlet-mapping.cofaxCDS")
	@JBExpect(strings = "/")
	Optional<String> getServletMappingAsOptional();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	Optional<String> getNonexistingAsOptional();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	String getNonexistingString();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	List<String> getNonexistingAsList();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	List<String> getNonexistingAsArray();

}
