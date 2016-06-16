/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses maven https://github.com/ferstl/depgraph-maven-plugin plugin to resolve the dependencies for the test class.
 * It relies on the dot graph generated by the depgraph-maven-plugin and the artifact should have set this plugin
 * in its maven build section and run it before the test is executed.
 * If the file doesn't exists it will thrown a {@link IllegalStateException} in all of its methods.
 */
public class DependencyGraphMavenDependenciesResolver implements MavenDependenciesResolver
{
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String DEPENDENCIES_GRAPH_ARROW = "->";
    private static final String DEPENDENCIES_GRAPH_FILE = "/target/test-classes/dependency-graph.dot";

    @Override
    public Map<MavenArtifact, Set<MavenArtifact>> buildDependencies(Class<?> testClass)
    {
        try
        {
            final File dependenciesGraphFile = getDependenciesGraphFile(testClass);
            if (!dependenciesGraphFile.exists())
            {
                throw new IllegalStateException(String.format("Unable to resolve dependencies for test due to file '%s' was not found", DEPENDENCIES_GRAPH_FILE));
            }

            Path dependenciesPath = Paths.get(dependenciesGraphFile.toURI());
            BasicFileAttributes view = Files.getFileAttributeView(dependenciesPath, BasicFileAttributeView.class).readAttributes();
            logger.debug("Building maven dependencies graph using depgraph-maven-plugin output file: '{}', created: {}, last modified: {}", dependenciesGraphFile, view.creationTime(), view.lastModifiedTime());


            Map<MavenArtifact, Set<MavenArtifact>> mavenArtifactsDependencies = new HashMap<>();
            Files.readAllLines(dependenciesGraphFile.toPath(),
                               Charset.defaultCharset()).stream()
                    .filter(line -> line.contains(DEPENDENCIES_GRAPH_ARROW)).forEach(line ->
                     {
                         MavenArtifact from = parseDotDependencyArtifactFrom(line);
                         MavenArtifact to = parseDotDependencyArtifactTo(line);
                         if (!mavenArtifactsDependencies.containsKey(from))
                         {
                             mavenArtifactsDependencies.put(from, new HashSet<>());
                         }
                         mavenArtifactsDependencies.get(from).add(to);
                     }
            );
            return mavenArtifactsDependencies;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error while processing dependencies from depgraph-maven-plugin output file", e);
        }
    }

    private File getDependenciesGraphFile(final Class<?> testClass)
    {
        String dependenciesListFileName = DEPENDENCIES_GRAPH_FILE;
        DependencyGraphMavenDependenciesResolverConfig annotation = testClass.getAnnotation(DependencyGraphMavenDependenciesResolverConfig.class);

        if (annotation != null)
        {
            dependenciesListFileName = annotation.dependenciesGraphFile();
        }

        final String userDir = System.getProperty("user.dir");
        return new File(userDir, dependenciesListFileName);
    }

    private MavenArtifact parseDotDependencyArtifactTo(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[1];
        if (artifactLine.contains("["))
        {
            artifactLine = artifactLine.substring(0, artifactLine.indexOf("["));
        }
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseDotDependencyArtifactFrom(final String line)
    {
        String artifactLine = line.split(DEPENDENCIES_GRAPH_ARROW)[0];
        if (artifactLine.contains("\""))
        {
            artifactLine = artifactLine.substring(artifactLine.indexOf("\"") + 1, artifactLine.lastIndexOf("\""));
        }
        return parseMavenArtifact(artifactLine.trim());
    }

    private MavenArtifact parseMavenArtifact(final String mavenDependencyString)
    {
        String[] tokens = mavenDependencyString.split(MavenArtifact.MAVEN_DEPENDENCIES_DELIMITER);
        String groupId = tokens[0];
        String artifactId = tokens[1];
        String type = tokens[2];
        String version = tokens[3];
        String scope = tokens[4];
        return new MavenArtifact(groupId, artifactId, type, version, scope);
    }

}
