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
package org.jsonbeam.intern.io;

import java.io.Closeable;

import org.jsonbeam.exceptions.UnexpectedEOF;
import org.jsonbeam.intern.index.keys.KeyReference;

/**
 * @author Sven
 */
public interface CharacterSource {

	boolean hasNext();

	char next();

	int getPosition();

	char nextConsumingWhitespace();

	int skipToQuote();

	/**
	 * @param a
	 * @return
	 */
	CharacterSource getSourceFromPosition(int a);

	default Closeable ioHandle() {
		return () -> {
		};
	}

	KeyReference parseJSONKey();

	/**
	 * 
	 */
	long skipToStringEnd();

	/**
	 * @return
	 */
	long findNull();

	/**
	 * @return
	 */
	long findTrue();

	/**
	 * @return
	 */
	long findFalse();

	void setCharsBuffer(KeyReference key);

	/**
	 * @return
	 */
	int getPrevPosition();

}
