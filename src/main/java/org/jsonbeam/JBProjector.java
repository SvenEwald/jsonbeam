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
package org.jsonbeam;

public class JBProjector {

	public enum Flags {
		/**
		 * Let the projections toString() method render the projection target as JSON. Be careful if your documents get large.
		 * toString() might be used frequently by the IDE your debugging in.
		 */
		TO_STRING_RENDERS_JSON,
		/**
		 * Option to strip empty values from the result.
		 */
		OMIT_EMPTY_NODES,
		/**
		 * If a node is not present, handle it like it is empty.
		 */
		ABSENT_IS_EMPTY,

	}

}
