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
import org.jsonbeam.exceptions.JBUnsupportedCharset;
import org.jsonbeam.intern.io.charsets.ByteArrayCharacterSourceCP1252;
import org.jsonbeam.intern.io.charsets.ByteArrayCharacterSourceISO88591;
import org.jsonbeam.intern.io.charsets.ByteArrayCharacterSourceUTF16BE;
import org.jsonbeam.intern.io.charsets.ByteArrayCharacterSourceUTF16LE;
import org.jsonbeam.intern.io.charsets.ByteArrayCharacterSourceUTF8;
import org.jsonbeam.intern.utils.IOHelper;
import org.jsonbeam.intern.utils.TriFunction;

/**
 * @author Sven
 */
public abstract class EncodedCharacterSource extends JsonCharacterSource {

	private Closeable closeable = ()->{};

	protected final byte[] buffer;
	
	static final Map<Charset,TriFunction<byte[],Integer,Integer,? extends EncodedCharacterSource>> FACTORIES=new HashMap<>();
	static {
		FACTORIES.put(StandardCharsets.ISO_8859_1, ByteArrayCharacterSourceISO88591::new );//"ISO-8859-1"
		FACTORIES.put(StandardCharsets.US_ASCII, ByteArrayCharacterSourceISO88591::new );//"US-ASCII"
		FACTORIES.put(StandardCharsets.UTF_8, ByteArrayCharacterSourceUTF8::new );//"UTF-8"
		FACTORIES.put(StandardCharsets.UTF_16LE, ByteArrayCharacterSourceUTF16LE::new );//"UTF-16LE"
		FACTORIES.put(StandardCharsets.UTF_16BE, ByteArrayCharacterSourceUTF16BE::new );//"UTF-16BE"
		FACTORIES.put(Charset.forName("windows-1252"), ByteArrayCharacterSourceCP1252::new);
	}

	public static EncodedCharacterSource handleBOM(byte[] buffer,final int offset, final int length, final Charset charset) {
		Objects.requireNonNull(buffer);
		Objects.requireNonNull(charset);
		// If charset==UTF_16, determine real charset via BOM
		if (IOHelper.UTF_BOM == charset || (charset.equals(StandardCharsets.UTF_16))) {
			Charset charSetForBOM = IOHelper.charSetForBOM(buffer,offset).orElseThrow(()->new JBIOException("For charset {0} a BOM is needed in the data, but was not found. I don't know how to read it.",charset.name()));
			TriFunction<byte[],Integer,Integer,? extends EncodedCharacterSource> factory=FACTORIES.get(charSetForBOM);
			if (factory==null) {
				throw new IllegalStateException(charSetForBOM.name());
			}
			EncodedCharacterSource source = factory.apply(buffer,(offset-1), length-1);
			// Skip over BOM bytes
			source.cursor += StandardCharsets.UTF_8.equals(charSetForBOM) ? 3 : 2;
			return source;
		}
		return FACTORIES.computeIfAbsent(charset,cs->{throw new JBUnsupportedCharset(cs.name());}).apply(buffer,offset-1, length-1);
	}

	protected EncodedCharacterSource(byte[] buffer,final int offset,final int length) {
		super(offset,length);
		this.buffer=buffer;
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

}
