/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.launcher.descriptor;

import org.mule.config.bootstrap.ArtifactRuntimeInfo;
import org.mule.module.launcher.artifact.Artifact;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a base description of an {@link Artifact}
 */
public class ArtifactDescriptor
{
    public static final String DEFAULT_DEPLOY_PROPERTIES_RESOURCE = "mule-deploy.properties";

    protected String name;
    protected boolean redeploymentEnabled = true;
    protected Set<String> loaderOverride = new HashSet<String>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<String> getLoaderOverride()
    {
        return loaderOverride;
    }

    public void setLoaderOverride(Set<String> loaderOverride)
    {
        this.loaderOverride = loaderOverride;
    }

    public boolean isRedeploymentEnabled()
    {
        return redeploymentEnabled;
    }

    public void setRedeploymentEnabled(boolean redeploymentEnabled)
    {
        this.redeploymentEnabled = redeploymentEnabled;
    }

    public ArtifactRuntimeInfo getRuntimeInfo()
    {
        return new ArtifactRuntimeInfo(getName(), isRedeploymentEnabled(), Collections.unmodifiableSet(getLoaderOverride()));
    }

    protected List<String> getLibraries(File libDirectory)
    {
        if (libDirectory.exists())
        {
            String[] libraries = libDirectory.list(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".jar");
                }
            });
            return Arrays.asList(libraries);
        }
        return new ArrayList<>(0);
    }
}
