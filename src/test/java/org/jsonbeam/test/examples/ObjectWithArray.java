package org.jsonbeam.test.examples;

import java.util.List;

import org.jsonbeam.jsonprojector.annotations.JBRead;
import org.jsonbeam.test.utils.JBExpect;

public interface ObjectWithArray {

	static String JSON = "{\r\n" + //
			"	array1:[a,b],\r\n" + //
			"	array2:[{},{}],\r\n" + //
			"	object1:{ a:1,b:null,c:true,}\r\n" + //
			"	emptyArray:[],\r\n" + //
			"	emptyObject:{}\r\n" + //
			"}";

	// @JBExpect({ "a", "b" })
	// @JBRead("$.array1[*]")
	// List<String> getArray2();

	@JBExpect(strings = { "a", "b" })
	@JBRead("$.array1[*]")
	List<String> getArray1();

	@JBExpect(strings = { "a" })
	@JBRead("$.array1[0]")
	List<String> getArray1A();

	@JBExpect(strings = "a")
	@JBRead("$.array1[0]")
	String getArray1Aa();

	@JBExpect(strings = { "b" })
	@JBRead("$.array1[1]")
	List<String> getArray1B();

	@JBExpect(strings = "b")
	@JBRead("$.array1[1]")
	String getArray1Ba();

	@JBExpect()
	@JBRead("$.emptyArray")
	List<String> getEmptyArray();

}
