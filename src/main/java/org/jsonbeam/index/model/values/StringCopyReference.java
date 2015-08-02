package org.jsonbeam.index.model.values;

import org.jsonbeam.index.model.Reference;


public final class StringCopyReference implements Reference {

	private CharSequence data;
	
	public StringCopyReference(StringBuilder sb) {
		assert sb!=null;
		this.data=sb;
	}

	@Override
	public String apply(final CharSequence array) {
		if (data instanceof StringBuilder) {
			data = data.toString();
		}
		return data.toString();
	}

}
