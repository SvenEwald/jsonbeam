package org.jsonbeam.jsonprojector.utils;

import java.util.Arrays;

public class CharArraySequence implements CharSequence {

	private final char[] buffer;

	public CharArraySequence(final char[] buffer) {
		this.buffer = buffer;
	}

	@Override
	public char charAt(final int index) {
		return buffer[index];
	}

	@Override
	public int length() {
		return buffer.length;
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return new CharArraySequence(Arrays.copyOfRange(buffer, start, end));
	}

}
