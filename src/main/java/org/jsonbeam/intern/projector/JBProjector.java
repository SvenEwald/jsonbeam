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
package org.jsonbeam.intern.projector;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jsonbeam.JsonProjector.Flags;
import org.jsonbeam.intern.index.JBQueries;
import org.jsonbeam.intern.index.JBSubQueries;
import org.jsonbeam.intern.index.model.IndexReference;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.parser.JSONParser;
import org.jsonbeam.intern.utils.ProjectionInterfaceHelper;

public class JBProjector {

	private final Map<Class<?>, Function<Stream<Reference>, Stream<?>>> GLOBAL_TYPE_CONVERTERS = new HashMap<>();

	//private final void initTypeConverterMap()
	{
		GLOBAL_TYPE_CONVERTERS.put(Boolean.class, (s) -> s.map(pos -> pos.apply()).map(str -> Boolean.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Boolean.TYPE, (s) -> s.map(pos -> pos.apply()).map(str -> Boolean.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Byte.class, (s) -> s.map(pos -> pos.apply()).map(str -> Byte.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Byte.TYPE, (s) -> s.map(pos -> pos.apply()).map(str -> Byte.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Character.class, (s) -> s.map(pos -> pos.apply()).map(str -> Character.valueOf(str.charAt(0))));
		GLOBAL_TYPE_CONVERTERS.put(Character.TYPE, (s) -> s.map(pos -> pos.apply()).map(str -> Character.valueOf(str.charAt(0))));
		GLOBAL_TYPE_CONVERTERS.put(Short.class, (s) -> s.map(pos -> pos.apply()).map(str -> Short.valueOf(Short.parseShort(str))));
		GLOBAL_TYPE_CONVERTERS.put(Short.TYPE, (s) -> s.map(pos -> pos.apply()).map(str -> Short.valueOf(Short.parseShort(str))));
		GLOBAL_TYPE_CONVERTERS.put(Integer.class, (s) -> s.map(pos -> pos.apply()).map(str -> Integer.valueOf(Integer.parseInt(str))));
		//		TYPECONVERTERS.put(Integer.TYPE, s -> s.map(pos -> pos.apply(json)).mapToInt(str -> Integer.parseInt(str)));
		GLOBAL_TYPE_CONVERTERS.put(Long.class, (s) -> s.map(pos -> pos.apply()).map(str -> Long.valueOf(Long.parseLong(str))));
		//		TYPECONVERTERS.put(Long.TYPE, s -> s.map(pos -> pos.apply(json)).mapToLong(str -> Long.parseLong(str)));
		GLOBAL_TYPE_CONVERTERS.put(Double.class, (s) -> s.map(pos -> pos.apply()).map(str -> Double.valueOf(Double.parseDouble(str))));
		//		TYPECONVERTERS.put(Double.TYPE, s -> s.map(pos -> pos.apply(json)).mapToDouble(str -> Double.parseDouble(str)));
		GLOBAL_TYPE_CONVERTERS.put(Float.class, (s) -> s.map(pos -> pos.apply()).map(str -> Float.valueOf(Float.parseFloat(str))));
		GLOBAL_TYPE_CONVERTERS.put(Float.TYPE, (s) -> s.map(pos -> pos.apply()).map(str -> Float.valueOf(Float.parseFloat(str))));

		GLOBAL_TYPE_CONVERTERS.put(String.class, (s) -> s.map(pos -> pos.apply()));

		// missing types: date, BigDecimal, Number
	}

	private final static Map<Class<?>, ProjectionType> knownProjectionTypes = new ConcurrentHashMap<>();

	public  JBQueries calculateQueriesForRootProjection(final ProjectionType projectionType) {
		JBQueries rootQueries = new JBQueries();
		//projectionType.getProjectionsMethods().map(m -> method2PathRef(rootQueries, m)).forEach(rootQueries::addQuery);
		for (ProjectionMethod pm : projectionType.getProjectionsMethods()) {
			if (pm.returnsSubProjection()) {
				rootQueries.addQuery(pm.getPathReferenceStack(), () -> calculateSubQueriesForSubProjection(rootQueries, class2ProjectionType(pm.getReturnType())));
			}
			else {
				rootQueries.addQuery(pm.getPathReferenceStack(), null);
			}
		}
		return rootQueries;
	}

	private JBSubQueries calculateSubQueriesForSubProjection(final JBQueries parent, final ProjectionType projectionType) {
		JBSubQueries queries = new JBSubQueries(parent);
		//projectionType.getProjectionsMethods().map(m -> method2PathRef(queries, m)).forEach(queries::addQuery);
		for (ProjectionMethod pm : projectionType.getProjectionsMethods()) {
			if (pm.returnsSubProjection()) {
				queries.addQuery(pm.getPathReferenceStack(), () -> calculateSubQueriesForSubProjection(queries, class2ProjectionType(pm.getReturnType())));
			}
			else {
				queries.addQuery(pm.getPathReferenceStack(), null);
			}
		}
		return queries;
	}

	//	private PathReferenceStack method2PathRef(final JBQueries rootQueries, final ProjectionMethod m) {
	//		final String pathForMethod = m.getPath();
	//		final Class<?> returnType = m.getReturnType();
	//		PathReferenceStack query = m.returnsSubProjection() ? PathReferenceStack.parse(pathForMethod, () -> calculateSubQueriesForSubProjection(rootQueries, class2ProjectionType(returnType))) : PathReferenceStack.parse(pathForMethod);
	//		return query;
	//	}

	private final Set<Flags> flags;

	public JBProjector(final Flags... optionalFlags) {
		this.flags = ProjectionInterfaceHelper.unfoldEnumArray(optionalFlags);
	}

	//	public CanEvaluateOrProject onJSONString(final CharSequence json) {
	//		return new CanEvaluateOrProject() {
	//			@Override
	//			public <T> T createProjection(final Class<T> type) {
	//				return projectJSONString(json, type);
	//			}
	//
	//			@Override
	//			public JPathEvaluator evalJPath(String jpath) {
	//				// TODO Auto-generated method stub
	//				return null;
	//			}
	//		};
	//	}

	@SuppressWarnings("unchecked")
	public <T> T projectCharacterSource(final CharacterSource json, final Class<T> projectionInterface) {
		Objects.requireNonNull(json, "Parameter json must not be null");
		Objects.requireNonNull(json, "Parameter projectionInterface must not be null");
		ProjectionType projectionType = class2ProjectionType(projectionInterface);
		JBQueries rootQueries = calculateQueriesForRootProjection(projectionType);

		//	assert rootQueries.dumpQueryGraph();

		JSONParser jsonParser =  JSONParser.fMethod.apply(json, rootQueries);
		IndexReference rootReference = jsonParser.createIndex();
		//assert rootQueries.dumpResults();
		final BCProjectionInvocationHandler projectionInvocationHandler = new BCProjectionInvocationHandler(rootQueries, this, projectionType);
		return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), projectionType.getImplementedInterfaces(), projectionInvocationHandler));

	}

	public ProjectionType class2ProjectionType(final Class<?> projectionInterface) {
		return knownProjectionTypes.computeIfAbsent(projectionInterface, ProjectionType::new);
	}

	@SuppressWarnings("unchecked")
	public <T> T projectReference(final IndexReference r, final Class<T> projectionInterface) {
		assert r.getSubCollector().isPresent();

		//rootQueries.dumpQueryGraph();
		ProjectionType projectionType = class2ProjectionType(projectionInterface);

		final BCProjectionInvocationHandler projectionInvocationHandler = new BCProjectionInvocationHandler(r.getSubCollector().get(), this, projectionType);
		return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), projectionType.getImplementedInterfaces(), projectionInvocationHandler));

	}

	public Function<Stream<Reference>, Stream<?>> getGlobalTypeConverter(final Class<?> effectiveReturnType) {
		return GLOBAL_TYPE_CONVERTERS.computeIfAbsent(effectiveReturnType, this::calcGlobalTypeConverter);
	}

	Function<Stream<Reference>, Stream<?>> calcGlobalTypeConverter(final Class<?> effectiveReturnType) {
		throw new IllegalArgumentException("Type converter for class " + effectiveReturnType + " not implemented yet");
	}

	/**
	 * 
	 */
	public static void dropAllCaches() {
		knownProjectionTypes.clear();
	}
}
