/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal.runtime;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import org.mule.api.MuleEvent;
import org.mule.extension.ExtensionManager;
import org.mule.extension.introspection.ConfigurationModel;
import org.mule.extension.introspection.ExtensionModel;
import org.mule.extension.introspection.OperationModel;
import org.mule.extension.introspection.ParameterModel;
import org.mule.extension.runtime.ConfigurationInstance;
import org.mule.module.extension.internal.runtime.config.LifecycleAwareConfigurationInstance;
import org.mule.module.extension.internal.runtime.resolver.ResolverSetResult;
import org.mule.module.extension.internal.util.ExtensionsTestUtils;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOperationContextTestCase extends AbstractMuleTestCase
{
    private static final String CONFIG_NAME = "config";
    private static final String PARAM_NAME = "param1";
    private static final String VALUE = "Do you want to build a snowman?";

    @Mock
    private ExtensionModel extensionModel;

    @Mock
    private ConfigurationModel configurationModel;

    @Mock
    private OperationModel operationModel;

    @Mock
    private ResolverSetResult resolverSetResult;

    @Mock
    private MuleEvent event;

    @Mock
    private ExtensionManager extensionManager;

    private Object configurationInstance = new Object();
    private ConfigurationInstance<Object> configuration;
    private DefaultOperationContext operationContext;


    @Before
    public void before()
    {
        configuration = new LifecycleAwareConfigurationInstance<>(CONFIG_NAME, configurationModel, configurationInstance, emptyList());
        Map<ParameterModel, Object> parametersMap = new HashMap<>();
        parametersMap.put(ExtensionsTestUtils.getParameter(PARAM_NAME, String.class), VALUE);
        when(resolverSetResult.asMap()).thenReturn(parametersMap);

        operationContext = new DefaultOperationContext(configuration, resolverSetResult, event);
    }

    @Test
    public void getParameter()
    {
        assertThat(operationContext.getParameter(PARAM_NAME), is(VALUE));
    }
}