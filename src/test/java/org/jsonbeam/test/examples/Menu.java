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

public interface Menu {

	@JBExpect(strings = { "File", "New", "Open", "Close" })
	@JBRead("..value")
	List<String> get1();

	@JBExpect(strings = { "New", "Open", "Close" })
	@JBRead("..popup..value")
	List<String> get2();

	@JBExpect(strings = { "New", "Open", "Close" })
	@JBRead("..popup.menuitem..value")
	List<String> get3();
}
