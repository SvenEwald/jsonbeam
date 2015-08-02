package org.jsonbeam.index.model.values;

import org.jsonbeam.index.model.Reference;



public class LiteralReference implements Reference {

	private final String value;

	public LiteralReference(final String value) {
		this.value = value;
	}

	@Override
	public String apply(final CharSequence array) {
		return value;
	}

	@Override
	public String toString() {		
		return value;
	}
}
