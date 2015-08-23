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
package org.jsonbeam.jsonprojector.utils.collections.cmap;


/**
 * @author se
 */
public class HashCMap<T> implements CharMap<T> {

	int mask = 127;
	private final Object[] buffer = new Object[mask + 1];

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharMap<T> put(final char c, final CHashTtrieNode<T> value) {
		int pos = c;
		while (buffer[pos & mask] == null) {
			++pos;
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CHashTtrieNode<T> get(final char c) {
		// TODO Auto-generated method stub
		return null;
	}

}
