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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.function.IntSupplier;


/**
 * @author sven
 */
public class FileCharacterSource extends BaseCharacterSourceImpl {

	private final FileChannel channel;
	private final MappedByteBuffer buffer;
	
	public FileCharacterSource(final File file, final Charset charset) {
		super(charset);
		try {
			this.channel = new RandomAccessFile(file, "r").getChannel();
			this.buffer=channel.map(MapMode.READ_ONLY, 0, channel.size());
			max=(int) channel.size()-1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private FileCharacterSource(final Charset charset, final int initialValue, final int max, final IntSupplier nextChar,FileChannel channel,MappedByteBuffer buffer) {
		super( charset,  initialValue, max,  nextChar);
		this.channel=channel;
		this.buffer=buffer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CharacterSource getSourceFromPosition(final int a) {
		return new FileCharacterSource(charset,initValue,max,nextChar,channel,buffer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected int getNextByte() {		
		return buffer.get(++cursor);
	}

}
