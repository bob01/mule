/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import static org.mule.functional.junit4.runners.AnnotationUtils.getAnnotationAttributeFrom;
import org.mule.runtime.module.artifact.classloader.ArtifactClassLoader;
import org.mule.runtime.module.artifact.classloader.MuleArtifactClassLoader;

import com.google.common.collect.Sets;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default Mule implementation that creates almost the same classloader hierarchy that is used by Mule when running
 * applications.
 * The classloaders created have the following hierarchy:
 * <ul>
 *     <li>Container: all the provided scope dependencies plus their dependencies (if they are not test) and java</li>
 *     <li>Plugin (optional): all the compile scope dependencies and their dependencies (only the ones with scope compile)</li>
 *     <li>Application: all the test scope dependencies and their dependencies if they are not defined to be excluded, plus the test dependencies
 *     from the compile scope dependencies (again if they are not excluded).</li>
 * </ul>
 */
public class MuleClassLoaderRunnerFactory implements ClassLoaderRunnerFactory
{
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ClassLoader createClassLoader(Class<?> klass, ArtifactUrlClassification artifactUrlClassification)
    {
        // Container classLoader
        logClassLoaderUrls("CONTAINER", artifactUrlClassification.getContainer());
        final TestContainerClassLoaderFactory testContainerClassLoaderFactory = new TestContainerClassLoaderFactory(getExtraBootPackages(klass));
        Set<String> containerExportedPackages = new HashSet<>();
        containerExportedPackages.addAll(testContainerClassLoaderFactory.getBootPackages());
        containerExportedPackages.addAll(testContainerClassLoaderFactory.getSystemPackages());
        ArtifactClassLoader classLoader = testContainerClassLoaderFactory.createContainerClassLoader(new URLClassLoader(artifactUrlClassification.getContainer().toArray(new URL[0])));

        // Plugin classloaders
        if (!artifactUrlClassification.getPlugins().isEmpty())
        {
            final Set<URL> pluginUrls = artifactUrlClassification.getPlugins().get(0);
            // Plugin classLoader
            logClassLoaderUrls("PLUGIN", pluginUrls);
            classLoader = new MuleArtifactClassLoader("plugin", pluginUrls.toArray(new URL[0]), classLoader.getClassLoader(), classLoader.getClassLoaderLookupPolicy());
        }

        // Application classLoader
        logClassLoaderUrls("APP", artifactUrlClassification.getApplication());
        classLoader = new MuleArtifactClassLoader("app", artifactUrlClassification.getApplication().toArray(new URL[0]), classLoader.getClassLoader(), classLoader.getClassLoaderLookupPolicy());

        return classLoader.getClassLoader();
    }

    private void logClassLoaderUrls(final String classLoaderName, final Set<URL> urls)
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder builder = new StringBuilder(classLoaderName).append(" classloader urls: [");
            urls.stream().forEach(e -> builder.append("\n").append(" ").append(e.getFile()));
            builder.append("\n]");
            logger.debug(builder.toString());
        }
    }

    private Set<String> getExtraBootPackages(Class<?> klass)
    {
        String extraPackages = getAnnotationAttributeFrom(klass, ArtifactClassLoaderRunnerConfig.class, "extraBootPackages");

        return Sets.newHashSet(extraPackages.split(","));
    }

}
