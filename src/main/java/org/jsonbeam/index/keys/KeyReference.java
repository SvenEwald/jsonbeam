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
