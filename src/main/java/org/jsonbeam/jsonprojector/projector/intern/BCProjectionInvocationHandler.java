package org.jsonbeam.jsonprojector.projector.intern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jsonbeam.index.JBResultProvider;
import org.jsonbeam.index.keys.PathReferenceStack;
import org.jsonbeam.index.model.IndexReference;
import org.jsonbeam.index.model.Reference;
import org.jsonbeam.jsonprojector.projector.BCJSONProjector;
import org.jsonbeam.jsonprojector.projector.ProjectionType;
import org.jsonbeam.jsonprojector.utils.ProjectionInterfaceHelper;

public class BCProjectionInvocationHandler implements InvocationHandler {

	private final static InvocationHandler DEFAULT_METHOD_INVOKER = (p, m, a) -> {
		assert m.isDefault();
		Constructor<?> constructor = MethodHandles.lookup().getClass().getDeclaredConstructor(Class.class);
		constructor.setAccessible(true);
		Lookup newLookupInstance = (Lookup) constructor.newInstance(m.getDeclaringClass());
		return newLookupInstance.in(m.getDeclaringClass()).unreflectSpecial(m, m.getDeclaringClass()).bindTo(p).invokeWithArguments(a);
	};
	private final static Set<Class<?>> SUPPORTED_GENERIC_TYPES = new HashSet<>(Arrays.asList(List.class, Optional.class, Stream.class, Set.class, Iterable.class));
	private final Map<Class<?>, BiFunction<CharSequence, Stream<Reference>, Stream<?>>> TYPE_CONVERTERS = new HashMap<>();
	private final Map<Method, InvocationHandler> handlers = new HashMap<>();
	private final JBResultProvider queries;
	private final BCJSONProjector projector;

	//	private final Class<?> projectionInterface;

	private final CharSequence json;

	private static final class PrimitivesHolder {
		private final static Map<Class<?>, Object> PRIMITIVE_DEFAULTS = new HashMap<>();
		static {
			PRIMITIVE_DEFAULTS.put(Boolean.TYPE, Boolean.FALSE);
			PRIMITIVE_DEFAULTS.put(Byte.TYPE, Byte.valueOf((byte) 0));
			PRIMITIVE_DEFAULTS.put(Short.TYPE, Short.valueOf((short) 0));
			PRIMITIVE_DEFAULTS.put(Integer.TYPE, Integer.valueOf(0));
			PRIMITIVE_DEFAULTS.put(Long.TYPE, Long.valueOf(0L));
			PRIMITIVE_DEFAULTS.put(Float.TYPE, Float.valueOf(0.0f));
			PRIMITIVE_DEFAULTS.put(Double.TYPE, Double.valueOf(0.0d));
		}
		private final static Map<Class<?>, Object> PRIMITIVE_ARRAY_DEFAULTS = new HashMap<>();
		static {
			PRIMITIVE_ARRAY_DEFAULTS.put(Boolean.TYPE, new boolean[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Byte.TYPE, new byte[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Short.TYPE, new short[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Integer.TYPE, new int[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Long.TYPE, new long[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Float.TYPE, new float[0]);
			PRIMITIVE_ARRAY_DEFAULTS.put(Double.TYPE, new double[0]);
		}
		private final static Map<Class<?>, BiFunction<CharSequence, Reference, Object>> PRIMITIVE_CONVERTERS = new HashMap<>();
		static {
			PRIMITIVE_CONVERTERS.put(Boolean.TYPE, (json, r) -> Boolean.valueOf(Reference.TRUE == r));
			PRIMITIVE_CONVERTERS.put(Byte.TYPE, (json, r) -> Byte.valueOf(r.apply(json)));
			PRIMITIVE_CONVERTERS.put(Short.TYPE, (json, r) -> Short.valueOf(r.apply(json)));
			PRIMITIVE_CONVERTERS.put(Integer.TYPE, (json, r) -> Integer.valueOf(r.apply(json)));
			PRIMITIVE_CONVERTERS.put(Long.TYPE, (json, r) -> Long.valueOf(r.apply(json)));
			PRIMITIVE_CONVERTERS.put(Float.TYPE, (json, r) -> Float.valueOf(r.apply(json)));
			PRIMITIVE_CONVERTERS.put(Double.TYPE, (json, r) -> Double.valueOf(r.apply(json)));
		}
	}

	//	private final BiFunction<Class<?>, Reference, Object> RESULT2SUBPROJECTION;

	private Object result2Subprojection(final Class<?> effectiveReturnType, final Reference r) {
		//RESULT2SUBPROJECTION = (effectiveReturnType, r) ->
		return projector.projectReference(json, (IndexReference) r, effectiveReturnType);
	}

	private static Function<Stream<?>, Object> unStreamerForReturnType(final Class<?> returnType) {
		if (List.class.equals(returnType)) {
			return s -> s.collect(Collectors.toList());
		}
		if (returnType.isArray()) {
			Class<?> componentType = returnType.getComponentType();
			return s -> s.toArray(l -> (Object[]) Array.newInstance(componentType, l));
		}
		if (Stream.class.equals(returnType)) {
			return s -> s;
		}
		if (Optional.class.equals(returnType)) {
			return Stream::findFirst;
		}
		if (Iterable.class.equals(returnType)) {
			return Stream::iterator;
		}
		if (Set.class.equals(returnType)) {
			return s -> s.collect(Collectors.toSet());
		}
		return s -> s.findFirst().orElse(null);
	}

	public BCProjectionInvocationHandler(final CharSequence json, final JBResultProvider q, final BCJSONProjector bcjsonProjector, final ProjectionType projectionInterface) {
		this.json = json;
		this.queries = q;
		this.projector = bcjsonProjector;
		//TODO: Make type converter independent from current json
		//initTypeConverterMap();
		//	Arrays.stream(projectionInterface.getDeclaredMethods()).filter(m -> Modifier.isPublic(m.getModifiers())).forEach(m -> handlers.put(m, createInvocationHandler(m)));
	}

	private InvocationHandler createInvocationHandler(final Method method) {
		if (method.isDefault()) {
			return DEFAULT_METHOD_INVOKER;
		}
		String searchPath = ProjectionInterfaceHelper.getJSONPathForMethod(method);
		final Class<?> rawReturnType = method.getReturnType();
		final Class<?> effectiveReturnType = determineEffectiveReturnType(method);

		PathReferenceStack pathStack = PathReferenceStack.parse(searchPath);

		List<Reference> results = queries.getResultsForPath(pathStack);
		if (effectiveReturnType.isPrimitive()) {
			if (rawReturnType.isArray()) {
				if (results.isEmpty()) {
					return (p, m, a) -> PrimitivesHolder.PRIMITIVE_ARRAY_DEFAULTS.get(effectiveReturnType);
				}
			}
			return (p, m, a) -> results.isEmpty() ? PrimitivesHolder.PRIMITIVE_DEFAULTS.get(effectiveReturnType) : PrimitivesHolder.PRIMITIVE_CONVERTERS.get(effectiveReturnType).apply(json, results.get(0));
		}

		BiFunction<CharSequence, Stream<Reference>, Stream<?>> typeConverter = TYPE_CONVERTERS.computeIfAbsent(effectiveReturnType, this::calcTypeConverter);
		BiFunction<CharSequence, Stream<Reference>, Object> invocationFunction = typeConverter.andThen(unStreamerForReturnType(rawReturnType));
		return (p, m, a) -> invocationFunction.apply(json, results.stream());
	}

	private Class<?> determineEffectiveReturnType(final Method m_) {
		Class<?> returnType = m_.getReturnType();
		if (returnType.isArray()) {
			return returnType.getComponentType();
		}
		if (SUPPORTED_GENERIC_TYPES.contains(m_.getReturnType())) {
			Type[] typeArguments = ((ParameterizedType) m_.getGenericReturnType()).getActualTypeArguments();
			if (typeArguments.length < 1) {
				throw new IllegalArgumentException("Method " + m_ + " needs to declare the generic return type. I don't know what to use a component type.");
			}
			Type type = ((ParameterizedType) m_.getGenericReturnType()).getActualTypeArguments()[0];
			if (!(type instanceof Class)) {
				throw new IllegalArgumentException("Method " + m_ + " has unsupported return type.");
			}
			return (Class<?>) type;
		}
		return returnType;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return handlers.computeIfAbsent(method, this::createInvocationHandler).invoke(proxy, method, args);
		//return handlers.getOrDefault(method, ERROR_INVOKER).invoke(proxy, method, args);
	}

	private BiFunction<CharSequence, Stream<Reference>, Stream<?>> calcTypeConverter(final Class<?> effectiveReturnType) {
		if (ProjectionInterfaceHelper.isProjectionInterface(effectiveReturnType)) {
			return (json, s) -> s.filter(r -> r instanceof IndexReference).map(r -> result2Subprojection(effectiveReturnType, r));
		}
		return projector.getGlobalTypeConverter(effectiveReturnType);
		//
	}
}