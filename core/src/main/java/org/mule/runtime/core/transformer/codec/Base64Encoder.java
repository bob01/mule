/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.transformer.codec;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.transformer.TransformerException;
import org.mule.runtime.core.config.i18n.CoreMessages;
import org.mule.runtime.core.transformer.AbstractTransformer;
import org.mule.runtime.core.util.Base64;
import org.mule.runtime.core.util.IOUtils;

import java.io.InputStream;

/**
 * <code>Base64Encoder</code> transforms strings or byte arrays into Base64 encoded
 * string.
 */
public class Base64Encoder extends AbstractTransformer
{

    public Base64Encoder()
    {
        registerSourceType(DataType.STRING);
        registerSourceType(DataType.BYTE_ARRAY);
        registerSourceType(DataType.INPUT_STREAM);
        setReturnDataType(DataType.STRING);
    }

    @Override
    public Object doTransform(Object src, String encoding) throws TransformerException
    {
        try
        {
            byte[] buf;

            if (src instanceof String)
            {
                buf = ((String) src).getBytes(encoding);
            }
            else if (src instanceof InputStream)
            {
                InputStream input = (InputStream) src;
                try
                {
                    buf = IOUtils.toByteArray(input);
                }
                finally
                {
                    input.close();
                }
            }
            else
            {
                buf = (byte[]) src;
            }

            String result = Base64.encodeBytes(buf, Base64.DONT_BREAK_LINES);

            if (getReturnDataType().getType().equals(byte[].class))
            {
                return result.getBytes(encoding);
            }
            else
            {
                return result;
            }
        }
        catch (Exception ex)
        {
            throw new TransformerException(
                CoreMessages.transformFailed(src.getClass().getName(), "base64"), this, ex);
        }
    }

}
