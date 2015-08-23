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
 * @author Sven
 */
public class SingleValueCMap<T> implements CharMap<T> {

	private final char c;
	private final CHashTtrieNode<T> value;

	/**
	 * @param c
	 * @param value
	 */
	public SingleValueCMap(final char c, final CHashTtrieNode<T> value) {
		this.c = c;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharMap<T> put(final char c, final CHashTtrieNode<T> value) {
		CharMap<T> charMap = new HashCMap<T>();
		charMap.put(this.c, this.value);
		charMap.put(c, value);
		return charMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CHashTtrieNode<T> get(final char c) {
		return this.c == c ? value : null;
	}

}
