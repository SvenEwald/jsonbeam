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
package org.jsonbeam.index.keys;

import org.jsonbeam.io.CharacterSource;
import org.jsonbeam.io.StringCharacterSource;

public class KeyReference implements ElementKey {
	final int hash;
	final int start;
	//	final int end;
	final int length;
	final CharacterSource buffer;

	public KeyReference(final int start, final int length, final int hash, final CharacterSource buffer) {
		this.start = start;
		this.length = length;
		this.hash = hash;
		this.buffer = buffer;
	}

	public KeyReference(final String string) {
		this.start = 0;
		this.length = string.length();
		this.hash = string.hashCode();
		this.buffer = new StringCharacterSource(string);
	}

	@Override
	public boolean equals(final Object o) {
		if (o.hashCode() != hash) {
			return false;
		}

		if (this == o) {
			return true;
		}

		if (!(o instanceof KeyReference)) {
			return false;
		}
		KeyReference other = (KeyReference) o;
		if (other.length() != length()) {
			return false;
		}
		CharacterSource ob = other.buffer;
		int a = start;
		int b = other.start;
		CharacterSource asource = buffer.getSourceFromPosition(start);
		CharacterSource bsource = other.buffer.getSourceFromPosition(other.start);
		for (int i = 0; i < length(); i++) {
			if (asource.getNext() != bsource.getNext()) {
				return false;
			}
			//			if (ob.charAt(b++) != buffer.charAt(a++)) {
			//				return false;
			//			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public int length() {
		return length;// end - start;
	}

	@Override
	public boolean matches(final ElementKey otherKey) {
		return equals(otherKey);
	}

	@Override
	public String toString() {
		return buffer.getSourceFromPosition(start).asCharSequence(length).toString();
		//return buffer.asCharSequence(length).toString();//new StringBuilder(buffer.subSequence(start, start+length)).toString();
		//return String.valueOf(buffer, start, length());
	}
}
