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
package org.jsonbeam.intern.io.charsets;

import java.nio.charset.StandardCharsets;

import org.jsonbeam.intern.io.EncodedCharacterSource;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.utils.CP1252;

/**
 * @author sven
 */
public abstract class BytesCharacterSourceCP1252 extends EncodedCharacterSource {
	
	public BytesCharacterSourceCP1252(int offset,int length) {
		super(offset,length,StandardCharsets.US_ASCII);
	};
	
	@Override
	public int getNextByte() {
		//int a = buffer[++cursor] & 0xFF;
		return CP1252.toUTF16[readNextByte() & 0xff];
	}
	
	@Override
	public int getPrevPosition() {
		return cursor-1;
	}
	

}
