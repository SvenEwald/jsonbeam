package org.jsonbeam.jsonprojector.projector;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import java.lang.reflect.Proxy;

import org.jsonbeam.JBProjector.Flags;
import org.jsonbeam.index.JBQueries;
import org.jsonbeam.index.JBSubQueries;
import org.jsonbeam.index.model.IndexReference;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.jsonprojector.parser.IterativeJSONParser;
import org.jsonbeam.jsonprojector.projector.intern.BCProjectionInvocationHandler;
import org.jsonbeam.jsonprojector.projector.intern.CanEvaluateOrProject;
import org.jsonbeam.jsonprojector.utils.ProjectionInterfaceHelper;

public class BCJSONProjector {

	private final Map<Class<?>, BiFunction<CharSequence, Stream<Reference>, Stream<?>>> GLOBAL_TYPE_CONVERTERS = new HashMap<>();

	//private final void initTypeConverterMap()
	{
		GLOBAL_TYPE_CONVERTERS.put(Boolean.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Boolean.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Boolean.TYPE, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Boolean.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Byte.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Byte.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Byte.TYPE, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Byte.valueOf(str)));
		GLOBAL_TYPE_CONVERTERS.put(Character.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Character.valueOf(str.charAt(0))));
		GLOBAL_TYPE_CONVERTERS.put(Character.TYPE, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Character.valueOf(str.charAt(0))));
		GLOBAL_TYPE_CONVERTERS.put(Short.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Short.valueOf(Short.parseShort(str))));
		GLOBAL_TYPE_CONVERTERS.put(Short.TYPE, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Short.valueOf(Short.parseShort(str))));
		GLOBAL_TYPE_CONVERTERS.put(Integer.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Integer.valueOf(Integer.parseInt(str))));
		//		TYPECONVERTERS.put(Integer.TYPE, s -> s.map(pos -> pos.apply(json)).mapToInt(str -> Integer.parseInt(str)));
		GLOBAL_TYPE_CONVERTERS.put(Long.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Long.valueOf(Long.parseLong(str))));
		//		TYPECONVERTERS.put(Long.TYPE, s -> s.map(pos -> pos.apply(json)).mapToLong(str -> Long.parseLong(str)));
		GLOBAL_TYPE_CONVERTERS.put(Double.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Double.valueOf(Double.parseDouble(str))));
		//		TYPECONVERTERS.put(Double.TYPE, s -> s.map(pos -> pos.apply(json)).mapToDouble(str -> Double.parseDouble(str)));
		GLOBAL_TYPE_CONVERTERS.put(Float.class, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Float.valueOf(Float.parseFloat(str))));
		GLOBAL_TYPE_CONVERTERS.put(Float.TYPE, (json, s) -> s.map(pos -> pos.apply(json)).map(str -> Float.valueOf(Float.parseFloat(str))));

		GLOBAL_TYPE_CONVERTERS.put(String.class, (json, s) -> s.map(pos -> pos.apply(json)));

		// missing types: date, BigDecimal, Number
	}

	private final Map<Class<?>, ProjectionType> knownProjectionTypes = new ConcurrentHashMap<>();

	private JBQueries calculateQueriesForRootProjection(final ProjectionType projectionType) {
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

	public BCJSONProjector(final Flags... optionalFlags) {
		this.flags = ProjectionInterfaceHelper.unfoldEnumArray(optionalFlags);
	}

	public CanEvaluateOrProject onJSONString(final CharSequence json) {
		return new CanEvaluateOrProject() {
			@Override
			public <T> T createProjection(final Class<T> type) {
				return projectJSONString(json, type);
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T> T projectJSONString(final CharSequence json, final Class<T> projectionInterface) {
		Objects.requireNonNull(json, "Parameter json must not be null");
		Objects.requireNonNull(json, "Parameter projectionInterface must not be null");

		ProjectionType projectionType = class2ProjectionType(projectionInterface);
		JBQueries rootQueries = calculateQueriesForRootProjection(projectionType);

		//	assert rootQueries.dumpQueryGraph();

		IterativeJSONParser jsonParser = new IterativeJSONParser(json, rootQueries);
		Reference rootReference = jsonParser.createIndex().getRootReference();
		//assert rootQueries.dumpResults(json);
		final BCProjectionInvocationHandler projectionInvocationHandler = new BCProjectionInvocationHandler(json, rootQueries, this, projectionType);
		return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), projectionType.getImplementedInterfaces(), projectionInvocationHandler));

	}

	private ProjectionType class2ProjectionType(final Class<?> projectionInterface) {
		return knownProjectionTypes.computeIfAbsent(projectionInterface, ProjectionType::new);
	}

	@SuppressWarnings("unchecked")
	public <T> T projectReference(final CharSequence json, final IndexReference r, final Class<T> projectionInterface) {
		assert r.getSubCollector().isPresent();

		//rootQueries.dumpQueryGraph();
		ProjectionType projectionType = class2ProjectionType(projectionInterface);

		final BCProjectionInvocationHandler projectionInvocationHandler = new BCProjectionInvocationHandler(json, r.getSubCollector().get(), this, projectionType);
		return ((T) Proxy.newProxyInstance(projectionInterface.getClassLoader(), projectionType.getImplementedInterfaces(), projectionInvocationHandler));

	}

	public BiFunction<CharSequence, Stream<Reference>, Stream<?>> getGlobalTypeConverter(final Class<?> effectiveReturnType) {
		return GLOBAL_TYPE_CONVERTERS.computeIfAbsent(effectiveReturnType, this::calcGlobalTypeConverter);
	}

	BiFunction<CharSequence, Stream<Reference>, Stream<?>> calcGlobalTypeConverter(final Class<?> effectiveReturnType) {
		throw new IllegalArgumentException("Type converter for class " + effectiveReturnType + " not implemented yet");
	}
}
