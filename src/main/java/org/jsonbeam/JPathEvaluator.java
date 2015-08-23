/**
 *  Copyright 2015 Sven Ewald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jsonbeam;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Interface to build fluent API for the evaluation API.
 */
public interface JPathEvaluator {

	/**
	 * Evaluates the JPath as a boolean value. This method is just a shortcut for as(Boolean.TYPE);
	 *
	 * @return true when the selected value equals (ignoring case) 'true'
	 */
	boolean asBoolean();

	/**
	 * Evaluates the JPath as a int value. This method is just a shortcut for as(Integer.TYPE);
	 *
	 * @return int value of evaluation result.
	 */
	int asInt();

	/**
	 * Evaluates the JPath as a String value. This method is just a shortcut for as(String.class);
	 *
	 * @return String value of evaluation result.
	 */
	String asString();

	/**
	 * Evaluates the JPath as a Date value. This method is just a shortcut for as(Date.class); You probably want to specify '
	 * using ' followed by some formatting pattern consecutive to the JPath.
	 *
	 * @return Date value of evaluation result.
	 */
	Date asDate();

	/**
	 * Evaluate the JPath as a value of the given type.
	 *
	 * @param returnType
	 *            Possible values: primitive types (e.g. Short.Type),boxed types, projection interfaces, any class with a String
	 *            constructor or a String factory method
	 * @return a value of return type that reflects the evaluation result.
	 */
	<T> T as(Class<T> returnType);

	/**
	 * Evaluate the JPath as an array of the given type.
	 *
	 * @param componentType
	 *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any class with a String constructor
	 *            or a String factory method, and org.w3c.Node
	 * @return an array of return type that reflects the evaluation result.
	 */
	<T> T[] asArrayOf(Class<T> componentType);

	/**
	 * Evaluate the JPath as a list of the given type.
	 *
	 * @param componentType
	 *            Possible values: primitive types (e.g. Short.Type), Projection interfaces, any class with a String constructor
	 *            or a String factory method.
	 * @return List of return type that reflects the evaluation result.
	 */
	<T> List<T> asListOf(Class<T> componentType);

	<T> Stream<T> asStreamOf(Class<T> componentType);

}