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
package org.jsonbeam.utils;

import java.io.IOException;

import org.jsonbeam.exceptions.JBIOException;

/**
 * @author Sven
 *
 */
@FunctionalInterface
public interface IOSupplier<T,E extends IOException> {

	T getThrowing() throws E;
	
	default T get() {
		try {
			return getThrowing();
		} catch (IOException e) {
			throw new JBIOException(e);
		}
	}
}
