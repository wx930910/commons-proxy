/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.proxy2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.proxy2.provider.BeanProvider;
import org.apache.commons.proxy2.provider.ConstantProvider;
import org.apache.commons.proxy2.provider.SingletonProvider;
import org.apache.commons.proxy2.util.AbstractTestCase;
import org.apache.commons.proxy2.util.DuplicateEcho;
import org.apache.commons.proxy2.util.Echo;
import org.apache.commons.proxy2.util.EchoImpl;
import org.apache.commons.proxy2.util.SuffixInterceptor;
import org.junit.Test;

@SuppressWarnings("serial")
public abstract class AbstractProxyFactoryTestCase extends AbstractTestCase {
	// **********************************************************************************************************************
	// Fields
	// **********************************************************************************************************************

	public static Invoker mockInvoker1() throws Throwable {
		Object[][] mockFieldVariableArgs = new Object[1][];
		Object[] mockFieldVariableMethod = new Object[1];
		Object[] mockFieldVariableProxy = new Object[1];
		Invoker mockInstance = mock(Invoker.class);
		when(mockInstance.invoke(any(Object.class), any(Method.class), any(Object[].class))).thenAnswer((stubInvo) -> {
			Object proxy = stubInvo.getArgument(0);
			Method method = stubInvo.getArgument(1);
			Object[] args = stubInvo.getArgument(2);
			mockFieldVariableProxy[0] = proxy;
			mockFieldVariableMethod[0] = method;
			mockFieldVariableArgs[0] = args;
			return null;
		});
		return mockInstance;
	}

	public static Interceptor mockInterceptor2() throws Throwable {
		Object[][] mockFieldVariableArguments = new Object[1][];
		Method[] mockFieldVariableMethod = new Method[1];
		Object[] mockFieldVariableProxy = new Object[1];
		Class<?>[] mockFieldVariableInvocationClass = new Class[1];
		Interceptor mockInstance = mock(Interceptor.class);
		when(mockInstance.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			mockFieldVariableArguments[0] = methodInvocation.getArguments();
			mockFieldVariableMethod[0] = methodInvocation.getMethod();
			mockFieldVariableProxy[0] = methodInvocation.getProxy();
			mockFieldVariableInvocationClass[0] = methodInvocation.getClass();
			return methodInvocation.proceed();
		});
		return mockInstance;
	}

	public static Interceptor mockInterceptor1() throws Throwable {
		Interceptor mockInstance = mock(Interceptor.class);
		when(mockInstance.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			methodInvocation.getArguments()[0] = "something different";
			return methodInvocation.proceed();
		});
		return mockInstance;
	}

	private static final Class<?>[] ECHO_ONLY = new Class[] { Echo.class };
	protected final ProxyFactory factory;
	private static final Class<?>[] COMPARABLE_ONLY = new Class[] { Comparable.class };

	// **********************************************************************************************************************
	// Constructors
	// **********************************************************************************************************************

	protected AbstractProxyFactoryTestCase() {
		final ServiceLoader<ProxyFactory> serviceLoader = ServiceLoader.load(ProxyFactory.class);
		Iterator<ProxyFactory> iter = serviceLoader.iterator();
		if (iter.hasNext()) {
			this.factory = iter.next();
		} else {
			throw new RuntimeException("Unable to find proxy factory implementation.");
		}

	}

	// **********************************************************************************************************************
	// Other Methods
	// **********************************************************************************************************************

	private ObjectProvider<Echo> createSingletonEcho() {
		return new SingletonProvider<Echo>(new BeanProvider<Echo>(EchoImpl.class));
	}

	@Test
	public void testInterceptorHashCode() {
		final Echo proxy = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		assertEquals(proxy.hashCode(), System.identityHashCode(proxy));
	}

	@Test
	public void testInvokerHashCode() throws Exception, Throwable {
		final Echo proxy = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(), ECHO_ONLY);
		assertEquals(proxy.hashCode(), System.identityHashCode(proxy));
	}

	@Test
	public void testDelegatorHashCode() throws Exception {
		final Echo proxy = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		assertEquals(proxy.hashCode(), System.identityHashCode(proxy));
	}

	@Test
	public void testInterceptorEquals() {
		final Date date = new Date();
		final Comparable<?> proxy1 = factory.createInterceptorProxy(date, new NoOpMethodInterceptor(), COMPARABLE_ONLY);
		final Comparable<?> proxy2 = factory.createInterceptorProxy(date, new NoOpMethodInterceptor(), COMPARABLE_ONLY);
		assertEquals(proxy1, proxy1);
		assertFalse(proxy1.equals(proxy2));
		assertFalse(proxy2.equals(proxy1));
	}

	@Test
	public void testInvokerEquals() throws Exception, Throwable, Throwable {
		final Comparable<?> proxy1 = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(),
				COMPARABLE_ONLY);
		final Comparable<?> proxy2 = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(),
				COMPARABLE_ONLY);
		assertEquals(proxy1, proxy1);
		assertFalse(proxy1.equals(proxy2));
		assertFalse(proxy2.equals(proxy1));
	}

	@Test
	public void testDelegatorEquals() throws Exception {
		final Date date = new Date();
		final Comparable<?> proxy1 = factory.createDelegatorProxy(new ConstantProvider<Date>(date), COMPARABLE_ONLY);
		final Comparable<?> proxy2 = factory.createDelegatorProxy(new ConstantProvider<Date>(date), COMPARABLE_ONLY);
		assertEquals(proxy1, proxy1);
		assertFalse(proxy1.equals(proxy2));
		assertFalse(proxy2.equals(proxy1));
	}

	@Test
	public void testBooleanInterceptorParameter() throws Throwable {
		final Echo echo = factory.createInterceptorProxy(new EchoImpl(),
				AbstractProxyFactoryTestCase.mockInterceptor2(), ECHO_ONLY);
		assertFalse(echo.echoBack(false));
		assertTrue(echo.echoBack(true));
	}

	@Test
	public void testCanProxy() {
		assertTrue(factory.canProxy(Echo.class));
		assertFalse(factory.canProxy(EchoImpl.class));
	}

	@Test
	public void testChangingArguments() throws Throwable {
		final Echo proxy = factory.createInterceptorProxy(new EchoImpl(),
				AbstractProxyFactoryTestCase.mockInterceptor1(), ECHO_ONLY);
		assertEquals("something different", proxy.echoBack("whatever"));
	}

	@Test
	public void testCreateDelegatingProxy() {
		final Echo echo = factory.createDelegatorProxy(createSingletonEcho(), ECHO_ONLY);
		echo.echo();
		assertEquals("message", echo.echoBack("message"));
		assertEquals("ab", echo.echoBack("a", "b"));
	}

	@Test
	public void testCreateInterceptorProxy() throws Throwable {
		final Echo target = factory.createDelegatorProxy(createSingletonEcho(), ECHO_ONLY);
		final Echo proxy = factory.createInterceptorProxy(target, SuffixInterceptor.mockInterceptor1(" suffix"),
				ECHO_ONLY);
		proxy.echo();
		assertEquals("message suffix", proxy.echoBack("message"));
	}

	@Test
	public void testDelegatingProxyClassCaching() throws Exception {
		final Echo proxy1 = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		final Echo proxy2 = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		assertNotSame(proxy1, proxy2);
		assertSame(proxy1.getClass(), proxy2.getClass());
	}

	@Test
	public void testDelegatingProxyInterfaceOrder() {
		final Echo echo = factory.createDelegatorProxy(createSingletonEcho(), Echo.class, DuplicateEcho.class);
		final List<Class<?>> expected = new LinkedList<Class<?>>(
				Arrays.<Class<?>>asList(Echo.class, DuplicateEcho.class));
		final List<Class<?>> actual = new LinkedList<Class<?>>(Arrays.asList(echo.getClass().getInterfaces()));
		actual.retainAll(expected); // Doesn't alter order!
		assertEquals(expected, actual);
	}

	@Test
	public void testDelegatingProxySerializable() throws Exception {
		final Echo proxy = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		assertSerializable(proxy);
	}

	@Test
	public void testInterceptingProxyClassCaching() throws Exception {
		final Echo proxy1 = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		final Echo proxy2 = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		assertNotSame(proxy1, proxy2);
		assertSame(proxy1.getClass(), proxy2.getClass());
	}

	@Test
	public void testInterceptingProxySerializable() throws Exception {
		final Echo proxy = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		assertSerializable(proxy);
	}

	@Test(expected = IOException.class)
	public void testInterceptorProxyWithCheckedException() throws Exception {
		final Echo proxy = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		proxy.ioException();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInterceptorProxyWithUncheckedException() throws Exception {
		final Echo proxy = factory.createInterceptorProxy(new EchoImpl(), new NoOpMethodInterceptor(), ECHO_ONLY);
		proxy.illegalArgument();
	}

	@Test
	public void testInterfaceHierarchies() {
		final SortedSet<String> set = factory
				.createDelegatorProxy(new ConstantProvider<SortedSet<String>>(new TreeSet<String>()), SortedSet.class);
		set.add("Hello");
	}

	@Test
	public void testInvokerProxy() throws Exception, Throwable {
		final Invoker tester = mock(Invoker.class);
		Object[][] testerArgs = new Object[1][];
		Object[] testerMethod = new Object[1];
		Object[] testerProxy = new Object[1];
		when(tester.invoke(any(Object.class), any(Method.class), any(Object[].class))).thenAnswer((stubInvo) -> {
			Object proxy = stubInvo.getArgument(0);
			Method method = stubInvo.getArgument(1);
			Object[] args = stubInvo.getArgument(2);
			testerProxy[0] = proxy;
			testerMethod[0] = method;
			testerArgs[0] = args;
			return null;
		});
		final Echo echo = factory.createInvokerProxy(tester, ECHO_ONLY);
		echo.echoBack("hello");
		assertEquals(Echo.class.getMethod("echoBack", String.class), testerMethod[0]);
		assertSame(echo, testerProxy[0]);
		assertNotNull(testerArgs[0]);
		assertEquals(1, testerArgs[0].length);
		assertEquals("hello", testerArgs[0]);
	}

	@Test
	public void testInvokerProxyClassCaching() throws Exception, Throwable, Throwable {
		final Echo proxy1 = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(), ECHO_ONLY);
		final Echo proxy2 = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(), ECHO_ONLY);
		assertNotSame(proxy1, proxy2);
		assertSame(proxy1.getClass(), proxy2.getClass());
	}

	@Test
	public void testInvokerProxySerializable() throws Exception, Throwable {
		final Echo proxy = factory.createInvokerProxy(AbstractProxyFactoryTestCase.mockInvoker1(), ECHO_ONLY);
		assertSerializable(proxy);
	}

	@Test
	public void testMethodInvocationClassCaching() throws Exception, Throwable {
		final Interceptor tester = mock(Interceptor.class);
		Object[][] testerArguments = new Object[1][];
		Method[] testerMethod = new Method[1];
		Object[] testerProxy = new Object[1];
		Class<?>[] testerInvocationClass = new Class[1];
		when(tester.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			testerArguments[0] = methodInvocation.getArguments();
			testerMethod[0] = methodInvocation.getMethod();
			testerProxy[0] = methodInvocation.getProxy();
			testerInvocationClass[0] = methodInvocation.getClass();
			return methodInvocation.proceed();
		});
		final EchoImpl target = new EchoImpl();
		final Echo proxy1 = factory.createInterceptorProxy(target, tester, ECHO_ONLY);
		final Echo proxy2 = factory.createInterceptorProxy(target, tester, Echo.class, DuplicateEcho.class);
		proxy1.echoBack("hello1");
		final Class<?> invocationClass1 = testerInvocationClass[0];
		proxy2.echoBack("hello2");
		assertSame(invocationClass1, testerInvocationClass[0]);
	}

	@Test
	public void testMethodInvocationDuplicateMethods() throws Exception, Throwable {
		final Interceptor tester = mock(Interceptor.class);
		Object[][] testerArguments = new Object[1][];
		Method[] testerMethod = new Method[1];
		Object[] testerProxy = new Object[1];
		Class<?>[] testerInvocationClass = new Class[1];
		when(tester.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			testerArguments[0] = methodInvocation.getArguments();
			testerMethod[0] = methodInvocation.getMethod();
			testerProxy[0] = methodInvocation.getProxy();
			testerInvocationClass[0] = methodInvocation.getClass();
			return methodInvocation.proceed();
		});
		final EchoImpl target = new EchoImpl();
		final Echo proxy = factory.createInterceptorProxy(target, tester, Echo.class, DuplicateEcho.class);
		proxy.echoBack("hello");
		assertEquals(Echo.class.getMethod("echoBack", String.class), testerMethod[0]);
	}

	@Test
	public void testMethodInvocationImplementation() throws Exception, Throwable {
		final Interceptor tester = mock(Interceptor.class);
		Object[][] testerArguments = new Object[1][];
		Method[] testerMethod = new Method[1];
		Object[] testerProxy = new Object[1];
		Class<?>[] testerInvocationClass = new Class[1];
		when(tester.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			testerArguments[0] = methodInvocation.getArguments();
			testerMethod[0] = methodInvocation.getMethod();
			testerProxy[0] = methodInvocation.getProxy();
			testerInvocationClass[0] = methodInvocation.getClass();
			return methodInvocation.proceed();
		});
		final EchoImpl target = new EchoImpl();
		final Echo proxy = factory.createInterceptorProxy(target, tester, ECHO_ONLY);
		proxy.echo();
		assertNotNull(testerArguments[0]);
		assertEquals(0, testerArguments[0].length);
		assertEquals(Echo.class.getMethod("echo"), testerMethod[0]);
		assertSame(proxy, testerProxy[0]);
		proxy.echoBack("Hello");
		assertNotNull(testerArguments[0]);
		assertEquals(1, testerArguments[0].length);
		assertEquals("Hello", testerArguments[0]);
		assertEquals(Echo.class.getMethod("echoBack", String.class), testerMethod[0]);
		proxy.echoBack("Hello", "World");
		assertNotNull(testerArguments[0]);
		assertEquals(2, testerArguments[0].length);
		assertEquals("Hello", testerArguments[0]);
		assertEquals("World", testerArguments[0]);
		assertEquals(Echo.class.getMethod("echoBack", String.class, String.class), testerMethod[0]);
	}

	@Test
	public void testPrimitiveParameter() {
		final Echo echo = factory.createDelegatorProxy(createSingletonEcho(), ECHO_ONLY);
		assertEquals(1, echo.echoBack(1));
	}

	@Test(expected = IOException.class)
	public void testProxyWithCheckedException() throws Exception {
		final Echo proxy = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		proxy.ioException();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProxyWithUncheckedException() throws Exception {
		final Echo proxy = factory.createDelegatorProxy(new ConstantProvider<Echo>(new EchoImpl()), Echo.class);
		proxy.illegalArgument();
	}

	@Test
	public void testWithNonAccessibleTargetType() {
		final Echo proxy = factory.createInterceptorProxy(new PrivateEcho(), new NoOpMethodInterceptor(), ECHO_ONLY);
		proxy.echo();
	}

	// **********************************************************************************************************************
	// Inner Classes
	// **********************************************************************************************************************

	protected static class NoOpMethodInterceptor implements Interceptor, Serializable {
		@Override
		public Object intercept(Invocation methodInvocation) throws Throwable {
			return methodInvocation.proceed();
		}
	}

	private static class PrivateEcho extends EchoImpl {
	}
}
