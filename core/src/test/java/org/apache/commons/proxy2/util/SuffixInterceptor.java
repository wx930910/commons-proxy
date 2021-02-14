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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.proxy2.Interceptor;
import org.apache.commons.proxy2.Invocation;

@SuppressWarnings("serial")
public class SuffixInterceptor {
	// **********************************************************************************************************************
	// Fields
	// **********************************************************************************************************************

	public static Interceptor mockInterceptor1(String suffix) throws Throwable {
		String mockFieldVariableSuffix;
		Interceptor mockInstance = mock(Interceptor.class);
		mockFieldVariableSuffix = suffix;
		when(mockInstance.intercept(any(Invocation.class))).thenAnswer((stubInvo) -> {
			Invocation methodInvocation = stubInvo.getArgument(0);
			Object result = methodInvocation.proceed();
			if (result instanceof String) {
				result = ((String) result) + mockFieldVariableSuffix;
			}
			return result;
		});
		return mockInstance;
	}

	// **********************************************************************************************************************
	// Constructors
	// **********************************************************************************************************************

	// **********************************************************************************************************************
	// Interceptor Implementation
	// **********************************************************************************************************************

}
