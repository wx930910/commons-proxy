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
package org.apache.commons.proxy2.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.proxy2.Invocation;
import org.apache.commons.proxy2.ProxyUtils;

public class MockInvocation {
	// ----------------------------------------------------------------------------------------------------------------------
	// Fields
	// ----------------------------------------------------------------------------------------------------------------------

	public static Invocation mockInvocation1(Method method, Object returnValue, Object... arguments) {
		Object[] mockFieldVariableArguments;
		Object mockFieldVariableReturnValue;
		Method mockFieldVariableMethod;
		Invocation mockInstance = mock(Invocation.class);
		mockFieldVariableReturnValue = returnValue;
		mockFieldVariableArguments = ObjectUtils.defaultIfNull(ArrayUtils.clone(arguments), ProxyUtils.EMPTY_ARGUMENTS);
		mockFieldVariableMethod = method;
		try {
			when(mockInstance.getArguments()).thenAnswer((stubInvo) -> {
				return mockFieldVariableArguments;
			});
			when(mockInstance.proceed()).thenAnswer((stubInvo) -> {
				return mockFieldVariableReturnValue;
			});
			when(mockInstance.getMethod()).thenAnswer((stubInvo) -> {
				return mockFieldVariableMethod;
			});
		} catch (Throwable exception) {
			exception.printStackTrace();
		}
		return mockInstance;
	}

	// ----------------------------------------------------------------------------------------------------------------------
	// Constructors
	// ----------------------------------------------------------------------------------------------------------------------

	// ----------------------------------------------------------------------------------------------------------------------
	// Invocation Implementation
	// ----------------------------------------------------------------------------------------------------------------------

}
