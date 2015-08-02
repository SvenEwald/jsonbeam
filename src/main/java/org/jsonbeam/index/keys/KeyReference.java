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

public class KeyReference implements ElementKey {
	final int hash;
	final int start;
	final int end;
	final CharSequence buffer;

	public KeyReference(final int start, final int end, final int hash, final CharSequence buffer) {
		this.start = start;
		this.end = end;
		this.hash = hash;
		this.buffer = buffer;
	}

	public KeyReference(final String string) {
		this(0, string.length(), string.hashCode(), string);
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
		CharSequence ob = other.buffer;
		int a = start;
		int b = other.start;
		for (int i = 0; i < length(); i++) {
			if (ob.charAt(b++) != buffer.charAt(a++)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	public int length() {
		return end - start;
	}

	@Override
	public boolean matches(final ElementKey otherKey) {
		return equals(otherKey);
	}

	@Override
	public String toString() {
		return new StringBuilder(buffer.subSequence(start, end)).toString();
		//return String.valueOf(buffer, start, length());
	}
}
