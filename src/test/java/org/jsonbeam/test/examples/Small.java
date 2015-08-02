package org.jsonbeam.test.examples;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Small {

	@JBRead("debug")
	@JBExpect(strings = "on\toff")
	String getDebug();

	@JBRead("num")
	@JBExpect(ints = 1)
	int getNum();
}
