package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Menu {

	@JBExpect(strings = { "File", "New", "Open", "Close" })
	@JBRead("..value")
	List<String> get1();

	@JBExpect(strings = { "New", "Open", "Close" })
	@JBRead("..popup..value")
	List<String> get2();

	@JBExpect(strings = { "New", "Open", "Close" })
	@JBRead("..popup.menuitem..value")
	List<String> get3();
}
