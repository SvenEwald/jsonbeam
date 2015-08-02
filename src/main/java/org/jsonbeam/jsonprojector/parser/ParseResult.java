package org.jsonbeam.jsonprojector.parser;

import org.jsonbeam.index.model.Reference;

public class ParseResult  {

	private final Reference rootReference;
	private final int endPosition;

	public ParseResult(Reference reference, int cursor) {
		this.rootReference=reference;
		this.endPosition=cursor;
	}

	/**
	 * @return the endPosition
	 */
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * @return the rootReference
	 */
	public Reference getRootReference() {
		return rootReference;
	}

}
