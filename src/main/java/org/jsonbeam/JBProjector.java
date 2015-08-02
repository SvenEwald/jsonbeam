package org.jsonbeam;

public class JBProjector {

	public enum Flags {
		/**
		 * Let the projections toString() method render the projection target as JSON. Be careful if your documents get large.
		 * toString() might be used frequently by the IDE your debugging in.
		 */
		TO_STRING_RENDERS_JSON,
		/**
		 * Option to strip empty values from the result.
		 */
		OMIT_EMPTY_NODES,
		/**
		 * If a node is not present, handle it like it is empty.
		 */
		ABSENT_IS_EMPTY,

	}
	
}
