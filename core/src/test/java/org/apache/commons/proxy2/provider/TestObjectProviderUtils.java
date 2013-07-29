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

package org.apache.commons.proxy2.provider;

import org.apache.commons.proxy2.util.AbstractTestCase;
import org.apache.commons.proxy2.util.EchoImpl;

import java.util.Date;

public class TestObjectProviderUtils extends AbstractTestCase
{
    public void testBean() throws Exception
    {
        assertTrue(ObjectProviderUtils.bean(EchoImpl.class) instanceof BeanProvider);
    }

    public void testCloning() throws Exception
    {
        assertTrue(ObjectProviderUtils.cloning(new Date()) instanceof CloningProvider);
    }

    public void testConstant() throws Exception
    {
        assertTrue(ObjectProviderUtils.constant("Hello") instanceof ConstantProvider);
    }

    public void testNullValue() throws Exception
    {
        assertTrue(ObjectProviderUtils.nullValue() instanceof NullProvider);
    }

    public void testSingleton() throws Exception
    {
        assertTrue(ObjectProviderUtils.singleton(new ConstantProvider<Object>("Hello")) instanceof SingletonProvider);
    }
}
