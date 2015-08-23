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

import java.nio.charset.Charset;

/**
 * @author Sven
 */
public class BytesCharacterSource extends BaseCharacterSourceImpl {

	private final byte[] buffer;

	//private int offset;

	public BytesCharacterSource(final byte[] buffer, final int length, final Charset charset) {
		super(charset);
		this.buffer = buffer;
		//cursor=offset;
		//initValue=offset;
		max = length - 1;
	}

	@Override
	public int getNextByte() {
		return buffer[++cursor];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int pos) {
		BytesCharacterSource source = new BytesCharacterSource(buffer, buffer.length, charset);
		source.initValue = pos - 1;
		source.cursor = pos - 1;
		return source;
	}

}
