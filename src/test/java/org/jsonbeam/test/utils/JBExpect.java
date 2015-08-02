package org.jsonbeam.test.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JBExpect {

	//	String singleValue() default "NOVALUE";

	String[] strings() default {};

	boolean[] booleans() default {};

	byte[] bytes() default {};

	short[] shorts() default {};

	int[] ints() default {};

	long[] longs() default {};

	float[] floats() default {};

	double[] doubles() default {};

}
