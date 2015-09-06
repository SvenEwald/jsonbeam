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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jsonbeam.exceptions.JBIOException;
import org.jsonbeam.exceptions.JBUnsupportedCharser;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceASCII;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceCP1252;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceISO88591;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceUTF16BE;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceUTF16LE;
import org.jsonbeam.intern.io.charsets.buffers.ByteArrayCharacterSourceUTF8;
import org.jsonbeam.intern.utils.IOHelper;
import org.jsonbeam.intern.utils.TriFunction;

/**
 * @author Sven
 */
public abstract class EncodedCharacterSource extends JsonCharacterSource {


	private Closeable closeable = ()->{};
	
	
	static final Map<String,TriFunction<byte[],Integer,Integer,? extends EncodedCharacterSource>> FACTORIES=new HashMap<>();
	static {
		FACTORIES.put("ISO-8859-1", ByteArrayCharacterSourceISO88591::new );
		FACTORIES.put("US-ASCII", ByteArrayCharacterSourceASCII::new );
		FACTORIES.put("UTF-8", ByteArrayCharacterSourceUTF8::new );
		FACTORIES.put("UTF-16LE", ByteArrayCharacterSourceUTF16LE::new );
		FACTORIES.put("UTF-16BE", ByteArrayCharacterSourceUTF16BE::new );
		FACTORIES.put("windows-1252", ByteArrayCharacterSourceCP1252::new);
	}
	

	public static EncodedCharacterSource handleBOM(byte[] buffer,final int offset, final int length, final Charset charset) {
		Objects.requireNonNull(buffer);
		Objects.requireNonNull(charset);
		// If charset==UTF_16, determine real charset via BOM
		if (IOHelper.UTF_BOM == charset || (charset.equals(StandardCharsets.UTF_16))) {
			String charSetNameForBOM = IOHelper.charSetNameForBOM(buffer,offset).orElseThrow(()->new JBIOException("For charset {0} a BOM is needed in the data, but was not found. I don't know how to read it.",charset.name()));
			TriFunction<byte[],Integer,Integer,? extends EncodedCharacterSource> factory=FACTORIES.get(charSetNameForBOM);
			if (factory==null) {
				throw new IllegalStateException(charSetNameForBOM);
			}
			EncodedCharacterSource source = factory.apply(buffer,offset-1, length-1);
			// Skip over BOM bytes
			source.cursor += "UTF-8".equals(charSetNameForBOM) ? 3 : 2;
			return source;
		}
		return FACTORIES.computeIfAbsent(charset.name(),name->{throw new JBUnsupportedCharser(name);}).apply(buffer,offset-1, length-1);
	}
	
	

	protected EncodedCharacterSource(final int offset,final int length, final Charset charset) {
		super(charset,offset,length);
//		this.initValue=offset;
//		this.cursor+=offset;
//		max = length - 1;
	}
	

	/**
	 * @param closeable
	 */
	public void setAutoCloseable(Closeable closeable) {
		this.closeable=closeable;
	}

	
	/**
	 * @return the closeable
	 */
	public Closeable getCloseable() {
		return closeable;
	}

	protected abstract int readNextByte();
}
