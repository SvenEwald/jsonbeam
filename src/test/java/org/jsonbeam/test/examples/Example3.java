package org.jsonbeam.test.examples;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example3 {
	// {"domain":"javacodegeeks.com","members":200,"names":["John","Jack","James"]}

	@JBRead("domain")
	@JBExpect(strings = "javacodegeeks.com")
	String getDomain();

}
