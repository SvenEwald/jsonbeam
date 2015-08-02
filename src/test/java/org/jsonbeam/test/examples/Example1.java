package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example1 {

	@JBExpect(strings = "/WEB-INF/tlds/cofax.tld")
	@JBRead("web-app.taglib.taglib-location")
	String getLastElement();

	@JBExpect(strings = { "cofaxCDS", "cofaxEmail", "cofaxAdmin", "fileServlet", "cofaxTools" })
	@JBRead("..servlet-name")
	List<String> getServeletNames();

	@JBExpect(strings = "false")
	@JBRead("..init-param..useJSP")
	String getUseJSP();
}
