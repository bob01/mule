/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import java.net.URL;

public class ArtifactClassSpace
{

    private final URL[] container;
    private final URL[][] plugins;
    private final URL[]application;

    public ArtifactClassSpace(URL[] container, URL[][] plugins, URL[] application)
    {
        this.container = container;
        this.plugins = plugins;
        this.application = application;
    }

    public URL[] getContainer()
    {
        return container;
    }

    public URL[][] getPlugins()
    {
        return plugins;
    }

    public URL[] getApplication()
    {
        return application;
    }
}
