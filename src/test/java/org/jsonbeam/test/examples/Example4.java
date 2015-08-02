package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface Example4 {

	@JBRead("[0]")
	Example4 getFirstEntry();

	@JBRead("[*]")
	List<String> getStrings();

	// Projecions to arrays not supported yet
	//	@JBExpect({ "xxx", "yyy" })
	//	default List<String> getArrayInArray() {
	//		Example4 firstEntry = getFirstEntry();
	//		Assert.assertNotNull(firstEntry);
	//		Example4 secondEntry = firstEntry.getFirstEntry();
	//		Assert.assertNotNull(secondEntry);
	//		return secondEntry.getStrings();
	//	}

	@JBRead("[0].[0].[0]")
	@JBExpect(strings = "xxx")
	String getFirstString();
}
