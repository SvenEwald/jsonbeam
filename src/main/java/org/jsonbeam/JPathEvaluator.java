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

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
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
	Optional<Boolean> asBoolean();

	/**
	 * Evaluates the JPath as a int value. This method is just a shortcut for as(Integer.TYPE);
	 *
	 * @return int value of evaluation result.
	 */
	OptionalInt asInt();

	
	/**
	 * Evaluates the JPath as a long value. This method is just a shortcut for as(Integer.TYPE);
	 *
	 * @return OptionalLong value of evaluation result.
	 */
	OptionalLong asLong();
	
	/**
	 * Evaluates the JPath as a double value. This method is just a shortcut for as(Integer.TYPE);
	 *
	 * @return OptionalDouble value of evaluation result.
	 */
	OptionalDouble asDouble();
	
	/**
	 * Evaluates the JPath as a String value. This method is just a shortcut for as(String.class);
	 *
	 * @return String value of evaluation result.
	 */
	Optional<String> asString();

	/**
	 * Evaluate the JPath as a value of the given type.
	 *
	 * @param returnType
	 *            Possible values: primitive types (e.g. Short.Type),boxed types, projection interfaces, any class with a String
	 *            constructor or a String factory method
	 * @return a value of return type that reflects the evaluation result.
	 */
	<T> Optional<T> as(Class<T> returnType);

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

	/**
	 * Evaluate the JPath as Stream of the given type.
	 * @param componentType
	 * @return Stream
	 */
	<T> Stream<T> asStreamOf(Class<T> componentType);
	
}