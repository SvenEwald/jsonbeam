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

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.JPathEvaluator;
import org.jsonbeam.intern.index.JBQueries;
import org.jsonbeam.intern.index.JBResultProvider;
import org.jsonbeam.intern.index.keys.PathReferenceStack;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.index.model.values.LiteralReference;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.parser.IndexOnlyJSONParser;
import org.jsonbeam.intern.projector.JBProjector;
import org.jsonbeam.intern.projector.ProjectionType;
import org.jsonbeam.intern.utils.ProjectionInterfaceHelper;

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
	private static List<Reference> evaluateNonProjection(String jpath, Supplier<CharacterSource> docProvider2) {
		PathReferenceStack parse = PathReferenceStack.parse(jpath);
		JBQueries queries = new JBQueries().addQuery(parse, null);
		new IndexOnlyJSONParser(docProvider2.get(), queries).createIndex();
		return queries.getResultsForPath(parse);
	}

	private static Optional<Reference> evaluateNonProjectionSingleValue(String jpath, Supplier<CharacterSource> docProvider2) {
		List<Reference> list = evaluateNonProjection(jpath, docProvider2);
		if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(0));
	}

	private List<Reference> evaluateResultsForSubprojection(Class<?> componentType) {
		ProjectionType projectionType = projector.class2ProjectionType(componentType);
		JBQueries rootQueries = new JBQueries();
		//JBSubQueries subQueries2 = ;
		PathReferenceStack path = PathReferenceStack.parse(jpath);
		//FIXME: Somehow reuse the same query? Avoid calculation for multiple invocations on same type.
		rootQueries.addQuery(path, () -> projector.calculateSubQueriesForSubProjection((rootQueries), projectionType));
		new IndexOnlyJSONParser(docProvider.get(), rootQueries).createIndex();
		List<Reference> resultsForPath = rootQueries.getResultsForPath(path);
		//rootQueries.dumpResults();
		return resultsForPath;
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
	public <T> Optional<T> as(Class<T> returnType) {
		
		List<Reference> resultsForPath;
		if (ProjectionInterfaceHelper.isProjectionInterface(returnType)) {
			resultsForPath = evaluateResultsForSubprojection(returnType);
		} else {
			resultsForPath=evaluateNonProjection(jpath, docProvider);
		}
		if (resultsForPath.isEmpty()) {
			return Optional.empty();
		}
		Function<Stream<Reference>, Stream<?>> typeConverter = projector.getGlobalTypeConverter(returnType);
		return (Optional<T>) typeConverter.apply(resultsForPath.stream()).findFirst();
		//		if (!(resultsForPath.get(0) instanceof IndexReference)) {
		//			throw new JBIOException("Can not map path '{0}' to type {1}.", jpath, returnType.getSimpleName());
		//		}
		//		T t = projector.projectReference((IndexReference) resultsForPath.get(0), returnType);
		//		return Optional.of(t);
	}
	
	public <T> Optional<T> as(Class<T> returnType,Function<JBResultProvider,T> constructor){
		projector.addProjectionConstructor(returnType,constructor);
		List<Reference> resultsForPath;
			resultsForPath = evaluateResultsForSubprojection(returnType);
		if (resultsForPath.isEmpty()) {
			return Optional.empty();
		}
		Function<Stream<Reference>, Stream<?>> typeConverter = projector.getGlobalTypeConverter(returnType);
		return (Optional<T>) typeConverter.apply(resultsForPath.stream()).findFirst();

	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> Stream<T> asStreamOf(Class<T> componentType) {
		Function<Stream<Reference>, Stream<?>> globalTypeConverter = projector.getGlobalTypeConverter(componentType);
		List<Reference> resultsForPath;
		if (ProjectionInterfaceHelper.isProjectionInterface(componentType)) {
			resultsForPath = evaluateResultsForSubprojection(componentType);
		}
		else {
			resultsForPath = evaluateNonProjection(jpath, docProvider);
		}
		return (Stream<T>) globalTypeConverter.apply(resultsForPath.stream());
		//return (Stream<T>) resultsForPath.stream().filter(r -> r instanceof IndexReference).map(ir -> projector.projectReference((IndexReference) ir, componentType));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> List<T> asListOf(Class<T> componentType) {
		return asStreamOf(componentType).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] asArrayOf(Class<T> componentType) {
		return asStreamOf(componentType).toArray(length -> (T[]) Array.newInstance(componentType, length));
	}

}
