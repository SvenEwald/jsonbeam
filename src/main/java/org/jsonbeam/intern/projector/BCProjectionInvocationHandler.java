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

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsonbeam.intern.index.JBResultProvider;
import org.jsonbeam.intern.index.keys.PathReferenceStack;
import org.jsonbeam.intern.index.model.IndexReference;
import org.jsonbeam.intern.index.model.Reference;
import org.jsonbeam.intern.utils.ProjectionInterfaceHelper;

public class BCProjectionInvocationHandler implements InvocationHandler {

	private final static InvocationHandler DEFAULT_METHOD_INVOKER = (p, m, a) -> {
		assert m.isDefault();
		Constructor<?> constructor = MethodHandles.lookup().getClass().getDeclaredConstructor(Class.class);
		constructor.setAccessible(true);
		Lookup newLookupInstance = (Lookup) constructor.newInstance(m.getDeclaringClass());
		return newLookupInstance.in(m.getDeclaringClass()).unreflectSpecial(m, m.getDeclaringClass()).bindTo(p).invokeWithArguments(a);
	};
	private final static Set<Class<?>> SUPPORTED_GENERIC_TYPES = new HashSet<>(Arrays.asList(List.class, Optional.class, Stream.class, Set.class, Iterable.class,Collection.class));
	private final Map<Class<?>, Function<Stream<Reference>, Stream<?>>> TYPE_CONVERTERS = new HashMap<>();
	private final Map<Method, InvocationHandler> handlers = new HashMap<>();
	private final JBResultProvider queries;
	private final JBProjector projector;

	//private final CharSequence json;

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
		private final static Map<Class<?>, Function<Reference, Object>> PRIMITIVE_CONVERTERS = new HashMap<>();
		static {
			PRIMITIVE_CONVERTERS.put(Boolean.TYPE, (r) -> Reference.TRUE == r);
			PRIMITIVE_CONVERTERS.put(Byte.TYPE, (r) -> Byte.valueOf(r.apply()));
			PRIMITIVE_CONVERTERS.put(Short.TYPE, (r) -> Short.valueOf(r.apply()));
			PRIMITIVE_CONVERTERS.put(Integer.TYPE, (r) -> Integer.valueOf(r.apply()));
			PRIMITIVE_CONVERTERS.put(Long.TYPE, (r) -> Long.valueOf(r.apply()));
			PRIMITIVE_CONVERTERS.put(Float.TYPE, (r) -> Float.valueOf(r.apply()));
			PRIMITIVE_CONVERTERS.put(Double.TYPE, (r) -> Double.valueOf(r.apply()));
		}

		private final static Map<Class<?>, BiConsumer<List<Reference>, Object>> PRIMITIVE_ARRAY_FILLER = new HashMap<>();
		static {
			PRIMITIVE_ARRAY_FILLER.put(Boolean.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((boolean[])o)[i++]=Reference.TRUE ==v;
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Byte.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((byte[])o)[i++]=Byte.valueOf(v.apply());
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Short.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((short[])o)[i++]=Short.valueOf(v.apply());
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Integer.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((int[])o)[i++]=Integer.valueOf(v.apply());
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Long.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((long[])o)[i++]=Long.valueOf(v.apply());
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Float.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((float[])o)[i++]=Float.valueOf(v.apply());
				}
			});
			PRIMITIVE_ARRAY_FILLER.put(Double.TYPE, (l, o) -> {
				int i=0;
				for (Reference v:l) {
					((double[])o)[i++]=Double.valueOf(v.apply());
				}
			});
		}		
	}

	//	private final BiFunction<Class<?>, Reference, Object> RESULT2SUBPROJECTION;

	private Object result2Subprojection(final Class<?> effectiveReturnType, final Reference r) {
		//RESULT2SUBPROJECTION = (effectiveReturnType, r) ->
		return projector.projectReference((IndexReference) r, effectiveReturnType);
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
			return s -> s.collect(Collectors.toList());
		}
		if (Collection.class.equals(returnType)) {
			return s->s.collect(Collectors.toList());
		}
		if (Set.class.equals(returnType)) {
			return s -> s.collect(Collectors.toSet());
		}
		return s -> s.findFirst().orElse(null);
	}

	public BCProjectionInvocationHandler(final JBResultProvider q, final JBProjector bcjsonProjector, final ProjectionType projectionInterface) {
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
				return (p, m, a) -> {
					if (results.isEmpty()) {
						return PrimitivesHolder.PRIMITIVE_ARRAY_DEFAULTS.get(effectiveReturnType);
					}
					Object array = Array.newInstance(effectiveReturnType, results.size());
					PrimitivesHolder.PRIMITIVE_ARRAY_FILLER.get(effectiveReturnType).accept(results, array);
					return array;
				};
			}
			return (p, m, a) -> results.isEmpty() ? PrimitivesHolder.PRIMITIVE_DEFAULTS.get(effectiveReturnType) : PrimitivesHolder.PRIMITIVE_CONVERTERS.get(effectiveReturnType).apply(results.get(0));
		}

		Function<Stream<Reference>, Stream<?>> typeConverter = TYPE_CONVERTERS.computeIfAbsent(effectiveReturnType, this::calcTypeConverter);
		Function<Stream<Reference>, Object> invocationFunction = typeConverter.andThen(unStreamerForReturnType(rawReturnType));
		return (p, m, a) -> invocationFunction.apply(results.stream());
	}

	private static Class<?> determineEffectiveReturnType(final Method m_) {
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

	private Function<Stream<Reference>, Stream<?>> calcTypeConverter(final Class<?> effectiveReturnType) {
		if (ProjectionInterfaceHelper.isProjectionInterface(effectiveReturnType)) {
			return s -> s.filter(r -> r instanceof IndexReference).map(r -> result2Subprojection(effectiveReturnType, r));
		}
		return projector.getGlobalTypeConverter(effectiveReturnType);
		//
	}
}
