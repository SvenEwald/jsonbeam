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
package org.jsonbeam.intern.evaluation;

import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jsonbeam.JPathEvaluator;
import org.jsonbeam.exceptions.JBUnimplemented;
import org.jsonbeam.intern.index.JBQueries;
import org.jsonbeam.intern.index.keys.PathReferenceStack;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.index.model.values.LiteralReference;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.parser.IndexOnlyJSONParser;
import org.jsonbeam.intern.projector.BCProjectionInvocationHandler;
import org.jsonbeam.intern.projector.JBProjector;
import org.jsonbeam.intern.projector.ProjectionType;

/**
 * @author Sven
 */
public class DefaultJPathEvaluator implements JPathEvaluator {

	final private JBProjector projector;
	final private String jpath;
	final private Supplier<CharacterSource> docProvider;

	/**
	 * @param projector
	 * @param jpath
	 * @param docProvider
	 */
	DefaultJPathEvaluator(final JBProjector projector, final String jpath, final Supplier<CharacterSource> docProvider) {
		this.projector = projector;
		this.jpath = jpath;
		this.docProvider = docProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Boolean> asBoolean() {
		Optional<Reference> reference = evaluateNonProjectionSingleValue(jpath, docProvider);
		if (reference.isPresent()) {
			if (reference.get() == LiteralReference.TRUE) {
				return Optional.of(Boolean.TRUE);
			}
			if (reference.get() == LiteralReference.FALSE) {
				return Optional.of(Boolean.FALSE);
			}
		}
		return Optional.empty();
	}

	/**
	 * @param jpath2
	 * @param docProvider2
	 * @return
	 */
	private List<Reference> evaluateNonProjection(String jpath, Supplier<CharacterSource> docProvider2) {
		PathReferenceStack parse = PathReferenceStack.parse(jpath);
		JBQueries queries = new JBQueries().addQuery(parse, null);
		new IndexOnlyJSONParser(docProvider2.get(), queries).createIndex();
		return queries.getResultsForPath(parse);
	}

	private Optional<Reference> evaluateNonProjectionSingleValue(String jpath, Supplier<CharacterSource> docProvider2) {
		List<Reference> list = evaluateNonProjection(jpath, docProvider2);
		if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OptionalInt asInt() {
		List<Reference> results = evaluateNonProjection(jpath, docProvider);
		if (results.isEmpty()) {
			return OptionalInt.empty();
		}
		Reference reference = results.get(0);
		return OptionalInt.of(Integer.parseInt(reference.apply()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OptionalLong asLong() {
		List<Reference> results = evaluateNonProjection(jpath, docProvider);
		if (results.isEmpty()) {
			return OptionalLong.empty();
		}
		Reference reference = results.get(0);
		return OptionalLong.of(Long.parseLong(reference.apply()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OptionalDouble asDouble() {
		List<Reference> results = evaluateNonProjection(jpath, docProvider);
		if (results.isEmpty()) {
			return OptionalDouble.empty();
		}
		Reference reference = results.get(0);
		return OptionalDouble.of(Double.parseDouble(reference.apply()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> asString() {
		List<Reference> results = evaluateNonProjection(jpath, docProvider);
		if (results.isEmpty()) {
			return Optional.empty();
		}
		Reference reference = results.get(0);
		return Optional.of(reference.apply());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date asDate() {
		throw new JBUnimplemented();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Optional<T> as(Class<T> returnType) {
		throw new JBUnimplemented();
//		ProjectionType projectionType = projector.class2ProjectionType(returnType);
//		JBQueries subqueries = projector.calculateQueriesForRootProjection(projectionType);
//		PathReferenceStack path = PathReferenceStack.parse(jpath);
////		new JBQueries().addQuery(PathReferenceStack., subqueries)
////		new IndexOnlyJSONParser(docProvider.get(), queries).createIndex();
//		final BCProjectionInvocationHandler projectionInvocationHandler = new BCProjectionInvocationHandler(queries, projector, projectionType);
//		return Optional.of((T) Proxy.newProxyInstance(returnType.getClassLoader(), projectionType.getImplementedInterfaces(), projectionInvocationHandler));
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T[] asArrayOf(Class<T> componentType) {
		throw new JBUnimplemented();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<T> asListOf(Class<T> componentType) {
		throw new JBUnimplemented();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Stream<T> asStreamOf(Class<T> componentType) {
		throw new JBUnimplemented();
	}

}
