/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.json.filters;

import org.mule.runtime.api.metadata.DataType;
import org.mule.runtime.core.api.MuleMessage;
import org.mule.runtime.core.api.routing.filter.Filter;
import org.mule.runtime.core.util.StringUtils;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter that will determine if the current message payload is a JSON encoded message.
 */
public class IsJsonFilter implements Filter
{

    /**
     * logger used by this class
     */
    protected transient final Logger logger = LoggerFactory.getLogger(IsJsonFilter.class);

    private boolean validateParsing = false;

    public IsJsonFilter()
    {
        super();
    }

    @Override
    public boolean accept(MuleMessage obj)
    {
        // TODO should be checking inbound IMO
        final String contentType = obj.getOutboundProperty("Content-Type", StringUtils.EMPTY);
        if (contentType.contains("application/json"))
        {
            return true;
        }
        try
        {
            return accept(obj.getMuleContext().getTransformationService().transform(obj, DataType.STRING).getPayload());
        }
        catch (Exception e)
        {
            logger.warn("Failed to read object payload as string for isJsonFilter", e);
            return false;
        }
    }

    public boolean accept(Object obj)
    {

        try
        {
            if (obj instanceof byte[])
            {
                obj = new String((byte[])obj);
            }

            if (obj instanceof String)
            {
                if (!mayBeJSON((String) obj))
                {
                    return false;
                }

                if (isValidateParsing())
                {
                    new ObjectMapper().readTree((String) obj);
                }
                return true;
            }
            else
            {
                return false;
            }

        }
        catch (IOException e)
        {
            logger.error("Filter result = false (message is not valid JSON): " + e.getMessage());
            return false;
        }
    }

    public boolean isValidateParsing()
    {
        return validateParsing;
    }

    public void setValidateParsing(boolean validateParsing)
    {
        this.validateParsing = validateParsing;
    }

    /**
     * Tests if the String possibly represents a valid JSON String.
     *
     * @param string Valid JSON strings are:
     *               <ul>
     *               <li>"null"</li>
     *               <li>starts with "[" and ends with "]"</li>
     *               <li>starts with "{" and ends with "}"</li>
     *               </ul>
     * @return true if the test string starts with one of the valid json characters
     */
    protected boolean mayBeJSON(String string)
    {
        return string != null
                && ("null".equals(string)
                || (string.startsWith("[") && string.endsWith("]")) || (string.startsWith("{") && string.endsWith("}")));
   }
}
