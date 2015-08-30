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
package org.jsonbeam.intern.index.keys;

import org.jsonbeam.intern.io.CharacterSource;

public class KeyReference implements ElementKey {
	final int hash;
	int start;
	final int length;
	CharacterSource buffer;
	char[] value;

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
		//this.buffer = new StringCharacterSource(string);
		this.buffer = null;
		this.value = string.toCharArray();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) { //FIXME optimize order, move value replacement to match method
			return true;
		}
		
		if (o.hashCode() != hash) {
			return false;
		}

		if (!(o instanceof KeyReference)) {
			return false;
		}

		KeyReference other = (KeyReference) o;
		if (other.length() != length()) {
			return false;
		}
		if (this.value == null) {
			buffer.setCharsBuffer(this);
		}
		if (other.value == null) {
			other.buffer.setCharsBuffer(other);
		}
		if ((other.value == this.value) && (other.start == this.start)) {
			return true;
		}
		int l = length;
		int a = this.start, b = other.start;
		char[] ac = this.value, bc = other.value;
		while (l-- > 0) {
			if (ac[a++] != bc[b++]) {
				return false;
			}
		}
		other.value = this.value;
		other.start = this.start;
		return true;
	}

	/**
	 * @return
	 */
	@Override
	public String toString() {
		if (value != null) {
			return new String(value);
		}
		buffer.setCharsBuffer(this);
		return new String(this.value);
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public int length() {
		return length;
	}

	@Override
	public boolean matches(final ElementKey otherKey) {
		return equals(otherKey);
	}

	/**
	 * @return
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param chars
	 */
	public void setChars(char[] chars, int start) {
		this.value = chars;
		this.start = start;
		this.buffer = null;
	}

}
