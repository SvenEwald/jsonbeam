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
package org.jsonbeam.io;

import org.jsonbeam.exceptions.UnexpectedEOF;

/**
 * @author Sven
 */
public interface CharacterSource {

	boolean hasNext();

	char getNext();

	int getPosition();

	default char nextConsumingWhitespace() {
		char c;
		while (hasNext()) {
			c = getNext();//json.charAt(j);
			if (c > ' ') {
				//cursor = j;
				return c;
			}

			//++j;
		}
		throw new UnexpectedEOF(getPosition());
	}

	/**
	 * @param a
	 * @return
	 */
	CharacterSource getSourceFromPosition(int a);

	/**
	 * Convert this CharacterSource to a String
	 * @param length
	 * @return
	 */
	default CharSequence asCharSequence(int length){
		final StringBuilder builder = new StringBuilder();
		for (int i=0;i<length;++i) {
			builder.append(getNext());
		}
		return builder;
	}
	
	default AutoCloseable ioHandle() {		
		return ()->{};
	}
	
}
