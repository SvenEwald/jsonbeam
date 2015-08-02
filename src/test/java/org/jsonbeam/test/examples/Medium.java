package org.jsonbeam.test.examples;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Medium {

	@JBRead("web-app.servlet[*].servlet-name")
	Stream<String> getAllServletNamesAsStream();

	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	default List<String> testGetAllServletNamesAsStream() {
		return getAllServletNamesAsStream().collect(Collectors.toList());
	}

	@JBRead("web-app.servlet[*].servlet-name")
	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	List<String> getAllServletNamesAsList();

	@JBRead("web-app.servlet[*].servlet-name")
	@JBExpect(strings = { "cofaxCDS", "cofaxAdmin", "cofaxTools" })
	String[] getAllServletNamesAsArray();

	@JBRead("web-app.servlet-mapping.cofaxCDS")
	@JBExpect(strings = "/")
	String getServletMapping();

	@JBRead("web-app.servlet-mapping.cofaxCDS")
	@JBExpect(strings = "/")
	Optional<String> getServletMappingAsOptional();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	Optional<String> getNonexistingAsOptional();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	String getNonexistingString();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	List<String> getNonexistingAsList();

	@JBRead("some.noneexisting.path")
	@JBExpect()
	List<String> getNonexistingAsArray();

}
