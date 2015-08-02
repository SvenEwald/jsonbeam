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

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example1 {

	@JBExpect(strings = "/WEB-INF/tlds/cofax.tld")
	@JBRead("web-app.taglib.taglib-location")
	String getLastElement();

	@JBExpect(strings = { "cofaxCDS", "cofaxEmail", "cofaxAdmin", "fileServlet", "cofaxTools" })
	@JBRead("..servlet-name")
	List<String> getServeletNames();

	@JBExpect(strings = "false")
	@JBRead("..init-param..useJSP")
	String getUseJSP();
}
