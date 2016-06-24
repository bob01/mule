/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.functional.junit4.runners;

import static org.mule.runtime.module.extension.internal.ExtensionProperties.EXTENSION_MANIFEST_FILE_NAME;
import org.mule.runtime.core.api.MuleContext;
import org.mule.runtime.core.api.lifecycle.InitialisationException;
import org.mule.runtime.core.config.builders.AbstractConfigurationBuilder;
import org.mule.runtime.extension.api.manifest.ExtensionManifest;
import org.mule.runtime.module.extension.internal.manager.DefaultExtensionManagerAdapterFactory;
import org.mule.runtime.module.extension.internal.manager.ExtensionManagerAdapter;
import org.mule.runtime.module.extension.internal.manager.ExtensionManagerAdapterFactory;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO
 */
public class ClassLoaderIsolatedExtensionsManagerConfigurationBuilder extends AbstractConfigurationBuilder
{

    private static Logger LOGGER = LoggerFactory.getLogger(ClassLoaderIsolatedExtensionsManagerConfigurationBuilder.class);

    private final ExtensionManagerAdapterFactory extensionManagerAdapterFactory;
    private final Class<?>[] extensionClasses;

    public ClassLoaderIsolatedExtensionsManagerConfigurationBuilder(Class<?>[] extensionClasses)
    {
        this(extensionClasses, new DefaultExtensionManagerAdapterFactory());
    }

    public ClassLoaderIsolatedExtensionsManagerConfigurationBuilder(Class<?>[] extensionClasses, ExtensionManagerAdapterFactory extensionManagerAdapterFactory)
    {
        this.extensionClasses = extensionClasses;
        this.extensionManagerAdapterFactory = extensionManagerAdapterFactory;
    }

    @Override
    protected void doConfigure(MuleContext muleContext) throws Exception
    {
        final ExtensionManagerAdapter extensionManager = createExtensionManager(muleContext);

        for (Class<?> extensionClass : extensionClasses)
        {
            //TODO: find a way to pass this folder name!
            ClassLoader extensionClassLoader = Class.forName(extensionClass.getName()).getClassLoader();
            URL manifestUrl = extensionClassLoader.getResource("META-INF/" + EXTENSION_MANIFEST_FILE_NAME);
            if (manifestUrl == null)
            {
                continue;
            }

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Discovered extension " + extensionClass.getName());
            }
            ExtensionManifest extensionManifest = extensionManager.parseExtensionManifestXml(manifestUrl);
            extensionManager.registerExtension(extensionManifest, extensionClassLoader);
        }
    }

    private ExtensionManagerAdapter createExtensionManager(MuleContext muleContext) throws InitialisationException
    {
        try
        {
            return extensionManagerAdapterFactory.createExtensionManager(muleContext);
        }
        catch (Exception e)
        {
            throw new InitialisationException(e, muleContext);
        }
    }
}
