package org.jsonbeam.index.model.values;

import org.jsonbeam.index.model.Reference;

public final class StringValueReference implements Reference {

	private final int end;
	private final int start;

	public StringValueReference(final int start, final int end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public String apply(final CharSequence array) {
		return new StringBuilder(array.subSequence(start, end)).toString();
		//return new String(array,start,end-start);
	}

}
