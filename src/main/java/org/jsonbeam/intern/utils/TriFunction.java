/**
 *    Copyright 2015 Sven Ewald
 *
 *    This file is part of JSONBeam.
 *
 *    JSONBeam is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, any
 *    later version.
 *
 *    JSONBeam is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JSONBeam.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jsonbeam.intern.utils;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result. This is the ternary specialization of
 *  {@link Function}.
 * @author Sven 
 *        
 * @param <A>
 *            the type of the first argument to the function
 * @param <B>
 *            the type of the second argument to the function
 * @param <C>
 *            the type of the third argument to the function
 * @param <R>
 *            the type of the result of the function
 * @see Function
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param a
	 *            the first function argument
	 * @param b
	 *            the second function argument
	 * @param c
	 *            the second function argument
	 * @return the function result
	 */
	R apply(A a, B b, C c);

	/**
	 * Returns a composed function that first applies this function to its input, and then applies the {@code after} function to
	 * the result. If evaluation of either function throws an exception, it is relayed to the caller of the composed function.
	 *
	 * @param <V>
	 *            the type of output of the {@code after} function, and of the composed function
	 * @param after
	 *            the function to apply after this function is applied
	 * @return a composed function that first applies this function and then applies the {@code after} function
	 * @throws NullPointerException
	 *             if after is null
	 */
	default <X> TriFunction<A, B, C,X> andThen(Function<? super R, ? extends X> after) {
		Objects.requireNonNull(after);
		return (A a, B b, C c) -> after.apply(apply(a, b, c));
	}

}
