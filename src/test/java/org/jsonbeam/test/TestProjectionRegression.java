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
package org.jsonbeam.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.net.URL;

import org.jsonbeam.intern.io.CharacterSource;
import org.jsonbeam.intern.io.CharsCharacterSource;
import org.jsonbeam.intern.io.StringCharacterSource;
import org.jsonbeam.intern.projector.JBProjector;
import org.jsonbeam.test.utils.JBExpect;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestProjectionRegression {

	private static final String EXAMPLE_PACKAGE = "org/jsonbeam/test/examples";

	private static Map<Class<?>, Function<JBExpect, Object>> ACCESSORS = new HashMap<>();
	static {
		ACCESSORS.put(String.class, JBExpect::strings);
		ACCESSORS.put(String[].class, JBExpect::strings);
		ACCESSORS.put(Boolean.class, JBExpect::booleans);
		ACCESSORS.put(Boolean.TYPE, JBExpect::booleans);
		ACCESSORS.put(Byte.class, JBExpect::bytes);
		ACCESSORS.put(Byte.TYPE, JBExpect::bytes);
		ACCESSORS.put(Short.class, JBExpect::shorts);
		ACCESSORS.put(Short.TYPE, JBExpect::shorts);
		ACCESSORS.put(Integer.class, JBExpect::ints);
		ACCESSORS.put(Integer.TYPE, JBExpect::ints);
		ACCESSORS.put(Long.class, JBExpect::longs);
		ACCESSORS.put(Long.TYPE, JBExpect::longs);
		ACCESSORS.put(Float.class, JBExpect::floats);
		ACCESSORS.put(Float.TYPE, JBExpect::floats);
		ACCESSORS.put(Double.class, JBExpect::doubles);
		ACCESSORS.put(Double.TYPE, JBExpect::doubles);
		ACCESSORS.put(Boolean[].class, JBExpect::booleans);
		ACCESSORS.put(Byte[].class, JBExpect::bytes);
		ACCESSORS.put(Short[].class, JBExpect::shorts);
		ACCESSORS.put(Integer[].class, JBExpect::ints);
		ACCESSORS.put(Long[].class, JBExpect::longs);
		ACCESSORS.put(Float[].class, JBExpect::floats);
		ACCESSORS.put(Double[].class, JBExpect::doubles);
		ACCESSORS.put(boolean[].class, JBExpect::booleans);
		ACCESSORS.put(byte[].class, JBExpect::bytes);
		ACCESSORS.put(short[].class, JBExpect::shorts);
		ACCESSORS.put(int[].class, JBExpect::ints);
		ACCESSORS.put(long[].class, JBExpect::longs);
		ACCESSORS.put(float[].class, JBExpect::floats);
		ACCESSORS.put(double[].class, JBExpect::doubles);

	}

	private static String accessField(final Field f) {
		try {
			return (String) f.get(null);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private static Class<?> name2Class(final String name) {
		try {
			return Class.forName(EXAMPLE_PACKAGE.replace('/', '.') + "." + name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Parameters(name = "{0}.{1}()")
	public static Iterable<Object[]> params() throws Exception {
		List<String> files = new LinkedList<>();
		Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(EXAMPLE_PACKAGE);
		while (resources.hasMoreElements()) {
			URL url = resources.nextElement();
			try (InputStream is = (InputStream) url.getContent()) {
				new Scanner(is).useDelimiter("\n").forEachRemaining(files::add);
			}
		}
		List<Object[]> tests = new LinkedList<>();
		List<String> names = files.stream().filter(s -> !s.startsWith("_")).filter(s -> s.endsWith(".class"))/* .filter(s -> files.contains(s.replace(".class",
																												* ".json"))) */.map(s -> s.substring(0, s.length() - ".class".length())).collect(Collectors.toList());
		names.forEach(name -> {
			Class<?> projectionInterface = name2Class(name);
			Arrays.stream(projectionInterface.getDeclaredMethods()).filter(m -> m.getAnnotation(JBExpect.class) != null).forEach(m -> {
				tests.add(new Object[] { name, m.getName(), projectionInterface, m });
			});
		});
		return tests;
	}

	private final String json;

	private final Class<?> projectionInterface;

	private final Method method;

	public TestProjectionRegression(final String name, final String methodName, final Class<?> projectionInterface, final Method method) throws Exception {
		this.json = Arrays.stream(projectionInterface.getDeclaredFields()).filter(f -> "JSON".equals(f.getName())).map(TestProjectionRegression::accessField).findAny().orElseGet(() -> {
			try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(EXAMPLE_PACKAGE + "/" + name + ".json")) {
				return new Scanner(is,"UTF-8").useDelimiter("\\A").next();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		this.projectionInterface = projectionInterface;
		this.method = method;
	}

	@Test
	public void testParseJsonString() throws Throwable {
		CharacterSource source = new StringCharacterSource(json);
		runParseJson(source);
	}
	
	@Test
	public void testParseJsonChars() throws Throwable {
		CharacterSource source = new CharsCharacterSource(json.toCharArray(),-1,json.length()-1);
		runParseJson(source);
	}
	
	private void runParseJson(CharacterSource source) throws Throwable {
		long testStart = System.currentTimeMillis();
		//CharacterSource source = new StringCharacterSource(json);
		Object createProjection = new JBProjector().projectCharacterSource(source, projectionInterface);
		long projectionCreated = System.currentTimeMillis();

		Function<JBExpect, Object> accessor = getAccessorForMethod(method);

		Object expected = accessor.apply(method.getAnnotation(JBExpect.class));

		method.setAccessible(true);
		MethodHandle methodHandle = MethodHandles.lookup().in(method.getDeclaringClass()).unreflect(method).bindTo(createProjection);
		long beforeProjectionCall = System.currentTimeMillis();
		Object result = methodHandle.invoke();
		long afterProjectionCall = System.currentTimeMillis();

		if (Optional.class.equals(method.getReturnType())) {
			result = ((Optional<?>) result).orElse(null);
		}

		try {
			ensureEquals(expected, result);
		} catch (AssertionError e) {
			ArrayList<StackTraceElement> stackTrace = new ArrayList<>(Arrays.asList(e.getStackTrace()));
			stackTrace.add(0, new StackTraceElement(method.getDeclaringClass().getName(), method.getName(), method.getDeclaringClass().getSimpleName() + ".java", 1));
			e.setStackTrace(stackTrace.toArray(new StackTraceElement[stackTrace.size()]));
			System.err.println("(" + method.getDeclaringClass().getSimpleName() + ".java:1)");
			throw e;
		}
		String m = method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()";
		System.out.print(source.getClass().getSimpleName()+"\t");
		System.out.println("Create Projection:" + (projectionCreated - testStart) + "ms.  \tCall:" + (afterProjectionCall - beforeProjectionCall) + "ms\t" + m);
	}

	private Function<JBExpect, Object> getAccessorForMethod(final Method method) {
		Class<?> returnType = method.getReturnType();
		if (method.getGenericReturnType() instanceof ParameterizedType) {
			returnType = (Class<?>) (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0]);
		}

		Function<JBExpect, Object> accessor = ACCESSORS.get(returnType);
		Assert.assertNotNull("No accessor for type" + returnType, accessor);
		return accessor;
	}

	@SuppressWarnings("rawtypes")
	private void ensureEquals(final Object expected, final Object result) {

		if (result == null) {
			Assert.assertTrue("unexpected null result", Array.getLength(expected) == 0);
			return;
		}
		if (result.getClass().isArray()) {
			assertMultiEquals(expected, Array::getLength, Array::get, result, Array::getLength, Array::get);
			return;
		}
		if (result instanceof List) {
			assertMultiEquals(expected, Array::getLength, Array::get, result, list -> ((List) list).size(), (list, p) -> ((List) list).get(p));
			return;
		}
		if (result instanceof Stream) {
			ensureEquals(expected, ((Stream<?>) result).toArray());
			return;
		}
		Assert.assertEquals("expected Type is " + expected.getClass() + " return type is " + result.getClass(), 1, Array.getLength(expected));
		Assert.assertEquals(Array.get(expected, 0), result);

	}

	private void assertMultiEquals(final Object expected, final Function<Object, Integer> elength, final BiFunction<Object, Integer, Object> egetter, final Object result, final Function<Object, Integer> rlength, final BiFunction<Object, Integer, Object> rGetter) {
		Integer len = elength.apply(expected);
		
		Assert.assertEquals(len, rlength.apply(result));
		for (int i = 0; i < len.intValue(); ++i) {
			Assert.assertEquals(egetter.apply(expected, i), rGetter.apply(result, i));
		}
		
	}
}
