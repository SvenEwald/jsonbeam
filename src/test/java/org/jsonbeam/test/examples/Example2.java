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

import org.jsonbeam.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example2 {

	@JBExpect(strings = "ISBN_10")
	@JBRead("$.items[0].volumeInfo.industryIdentifiers[0].type")
	String getFirstIdentifierType();

	@JBExpect(strings = "books#volume")
	@JBRead("$.items[0].kind")
	String getFirstItemKind();

	@JBExpect(strings = "books#volumes")
	@JBRead("$.kind")
	String getKind();

	@JBExpect(strings = "1")
	@JBRead("$.totalItems")
	String getTotalItems();
}
