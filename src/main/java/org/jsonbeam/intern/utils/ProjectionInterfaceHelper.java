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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.jsonbeam.annotations.JBRead;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public enum  ProjectionInterfaceHelper {
	;
	private final static Set<Class<?>> VALID_PARAMETERIZED_RETURN_TYPES = new HashSet<>(Arrays.asList(Set.class, List.class, Stream.class, Optional.class,Iterable.class,Collection.class));

	public static  Optional<RuntimeException> checkProjectionInterfaceType(final Class<?> projectionInterface) {
		if ((projectionInterface == null) || (!projectionInterface.isInterface()) || ((projectionInterface.getModifiers() & Modifier.PUBLIC) != Modifier.PUBLIC)) {
			return Optional.of(new IllegalArgumentException("Parameter " + projectionInterface + " is not a public interface."));
		}
		if (projectionInterface.isAnnotation()) {
			return Optional.of(new IllegalArgumentException("Parameter " + projectionInterface + " is an annotation interface. Remove the @ and try again."));
		}

		// Check projection methods
		for (Method method : projectionInterface.getMethods()) {
			final boolean isRead = (method.getAnnotation(JBRead.class) != null);

		}
		// for (Method method : projectionInterface.getMethods()) {
		// final boolean isRead = (method.getAnnotation(XBRead.class) != null);
		// final boolean isWrite = (method.getAnnotation(XBWrite.class) != null);
		// final boolean isDelete = (method.getAnnotation(XBDelete.class) != null);
		// final boolean isUpdate = (method.getAnnotation(XBUpdate.class) != null);
		// final boolean isExternal = (method.getAnnotation(XBDocURL.class) != null);
		// final boolean isThrowsException = (method.getExceptionTypes().length > 0);
		// if (isRead ? isUpdate || isWrite || isDelete : (isUpdate ? isWrite || isDelete : isWrite && isDelete)) {
		// throw new IllegalArgumentException("Method " + method + " has to many annotations. Decide for one of @" +
		// XBRead.class.getSimpleName() + ", @" + XBWrite.class.getSimpleName() + ", @" + XBUpdate.class.getSimpleName() +
		// ", or @" + XBDelete.class.getSimpleName());
		// }
		// if (isExternal && (isWrite || isUpdate || isDelete)) {
		// throw new IllegalArgumentException("Method " + method + " was declared as writing projection but has a @" +
		// XBDocURL.class.getSimpleName() +
		// " annotation. Defining external projections is only possible when reading because there is no DOM attached.");
		// }
		// if (isRead) {
		// if (!ReflectionHelper.hasReturnType(method)) {
		// throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() +
		// " annotation, but has no return type.");
		// }
		// if (ReflectionHelper.isRawType(method.getGenericReturnType())) {
		// throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() +
		// " annotation, but has a raw return type.");
		// }
		// if (method.getExceptionTypes().length > 1) {
		// throw new IllegalArgumentException("Method " + method + " has @" + XBRead.class.getSimpleName() +
		// " annotation, but declares to throw multiple exceptions. Which one should I throw?");
		// }
		// if (ReflectionHelper.isOptional(method.getReturnType()) && isThrowsException) {
		// throw new IllegalArgumentException("Method " + method +
		// " has an Optional<> return type, but declares to throw an exception. Exception will never be thrown because return value must not be null.");
		// }
		// }
		// if ((isWrite || isUpdate || isDelete) && isThrowsException) {
		// throw new IllegalArgumentException("Method " + method + " declares to throw exception " +
		// method.getExceptionTypes()[0].getSimpleName() +
		// " but is not a reading projection method. When should this exception be thrown?");
		// }
		// if (isWrite) {
		// if (!ReflectionHelper.hasParameters(method)) {
		// throw new IllegalArgumentException("Method " + method + " has @" + XBWrite.class.getSimpleName() +
		// " annotaion, but has no paramerter");
		// }
		// }
		// if (isUpdate) {
		// if (!ReflectionHelper.hasParameters(method)) {
		// throw new IllegalArgumentException("Method " + method + " has @" + XBUpdate.class.getSimpleName() +
		// " annotaion, but has no paramerter");
		// }
		// }
		// for (Class<?> clazz : method.getParameterTypes()) {
		// if (ReflectionHelper.isOptional(clazz)) {
		// throw new IllegalArgumentException("Method " + method +
		// " has java.util.Optional as a parameter type. You simply never should not do this.");
		// }
		// }
		// int count = 0;
		// for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
		// for (Annotation a : paramAnnotations) {
		// if (XBValue.class.equals(a.annotationType())) {
		// if (!(isWrite || isUpdate)) {
		// throw new IllegalArgumentException("Method " + method + " is not a writing projection method, but has an @" +
		// XBValue.class.getSimpleName() + " annotaion.");
		// }
		// if (count > 0) {
		// throw new IllegalArgumentException("Method " + method + " has multiple @" + XBValue.class.getSimpleName() +
		// " annotaions.");
		// }
		// ++count;
		// }
		// }
		// }
		// }
		return Optional.empty();
	}
	
	public static Class<?> findTargetComponentType(final Method method) {
		if (method.getReturnType().isArray()) {
			return method.getReturnType().getComponentType();
		}

		// if (!(ReflectionHelper.isStreamClass(method.getReturnType()) ||List.class.equals(method.getReturnType()))) {
		// return null;
		// }
		final Type type = method.getGenericReturnType();
		if (type instanceof Class) {
			return (Class<?>) type;
		}

		if (!VALID_PARAMETERIZED_RETURN_TYPES.contains(method.getReturnType())) {
			throw new IllegalArgumentException("I don't know how to create a '" + method.getReturnType().getSimpleName() + "'");
		}

		if (!(type instanceof ParameterizedType) || (((ParameterizedType) type).getActualTypeArguments() == null) || (((ParameterizedType) type).getActualTypeArguments().length < 1)) {
			throw new IllegalArgumentException("When using List as return type for method " + method + ", please specify a generic type for the List. Otherwise I do not know which type I should fill the List with.");
		}
		assert ((ParameterizedType) type).getActualTypeArguments().length == 1 : "";
		Type componentType = ((ParameterizedType) type).getActualTypeArguments()[0];
		if (!(componentType instanceof Class)) {
			throw new IllegalArgumentException("I don't know how to instantiate the generic type for the return type of method " + method);
		}
		return (Class<?>) componentType;
	}
	
	public static String getJSONPathForMethod(final Method m) {
		JBRead annotation = m.getAnnotation(JBRead.class);
		if (annotation==null) {
			throw new IllegalArgumentException("Expected method "+m+" to be a projection method. Did you forget to add an annotation?"); 
		}
		return annotation.value();
	}
	
	
	public  static Stream<Method> getProjectionMethods(final Class<?> projectionInterface) {
		return Arrays.stream(projectionInterface.getMethods()).filter(ProjectionInterfaceHelper::isProjectionMethod);
	}
	
	
	public static boolean isProjectionInterface(final Class<?> clazz) {
		return clazz.isInterface() && Arrays.stream(clazz.getDeclaredMethods()).filter(ProjectionInterfaceHelper::isProjectionMethod).findAny().isPresent();
	}
	
	public static boolean isProjectionMethod(final Method m) {
		return m.getAnnotation(JBRead.class) != null;
	}
	
	public static <T extends Enum<T>> Set<T> unfoldEnumArray(final T[] array) {
		if ((array == null) || (array.length == 0)) {
			return Collections.emptySet();
		}
		EnumSet<T> enumSet = EnumSet.of(array[0]);
		Arrays.stream(array).forEach(enumSet::add);
		return enumSet;
	}
}
