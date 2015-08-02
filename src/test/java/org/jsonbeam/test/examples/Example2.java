package org.jsonbeam.test.examples;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example2 {

	@JBExpect(strings = "ISBN_10")
	@JBRead("$.items[0].volumeInfo.industryIdentifiers[0].type")
	String getFirstIdentifierType();

	@JBExpect(strings = "books#volume")
	@JBRead("$.items[0].kind")
	String getFirstItemKind();

	@JBExpect(strings = "books#volumes")
	@JBRead("$.kind")
	String getKind();

	@JBExpect(strings = "1")
	@JBRead("$.totalItems")
	String getTotalItems();
}
