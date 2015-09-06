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
package org.jsonbeam.intern.io.charsets.buffers;

import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.charsets.BytesCharacterSourceASCII;
import org.jsonbeam.intern.io.charsets.BytesCharacterSourceUTF16BE;

/**
 * @author sven
 */
public class ByteArrayCharacterSourceUTF16BE extends BytesCharacterSourceUTF16BE {

	final private byte[] buffer;
	/**
	 * @param bytes
	 * @param offset
	 * @param length
	 */
	public ByteArrayCharacterSourceUTF16BE(byte[] bytes, int offset, int length) {
		super(offset, length);
		this.buffer = bytes;
	}
	

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int pos) 
	{
		ByteArrayCharacterSourceUTF16BE source = new ByteArrayCharacterSourceUTF16BE(buffer,pos,max);
		//source.cursor = pos;
		return source;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int readNextByte() {
		return buffer[++cursor];
	}

}
