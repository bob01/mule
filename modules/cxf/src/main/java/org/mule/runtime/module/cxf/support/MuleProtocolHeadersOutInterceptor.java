/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf.support;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static org.apache.cxf.message.Message.PROTOCOL_HEADERS;
import static org.mule.runtime.module.http.api.HttpConstants.Methods.POST;
import static org.mule.runtime.module.http.api.HttpConstants.RequestProperties.HTTP_METHOD_PROPERTY;
import static org.mule.runtime.module.http.api.HttpConstants.ResponseProperties.HTTP_STATUS_PROPERTY;
import org.mule.runtime.core.NonBlockingVoidMuleEvent;
import org.mule.runtime.core.api.MuleEvent;
import org.mule.runtime.core.api.MutableMuleMessage;
import org.mule.runtime.module.cxf.CxfConstants;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.AttachmentOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MuleProtocolHeadersOutInterceptor
    extends AbstractPhaseInterceptor<Message>
{

    private static final Logger logger = LoggerFactory.getLogger(MuleProtocolHeadersOutInterceptor.class);

    public MuleProtocolHeadersOutInterceptor()
    {
        super(Phase.PRE_STREAM);
        getAfter().add(AttachmentOutInterceptor.class.getName());
    }

    public void handleMessage(Message message) throws Fault
    {
        MuleEvent event = (MuleEvent) message.getExchange().get(CxfConstants.MULE_EVENT);
        
        if (event == null || event instanceof NonBlockingVoidMuleEvent)
        {
            return;
        }

        if (event.getMessage() == null)
        {
            return;
        }

        event.setMessage(event.getMessage().transform(msg -> {
            extractAndSetContentType(message, msg);
            extractAndSet(message, msg, Message.RESPONSE_CODE, HTTP_STATUS_PROPERTY);
            String method = (String) message.get(Message.HTTP_REQUEST_METHOD);
            final String finalMethod = method != null ? method : POST.name();
            msg.setOutboundProperty(HTTP_METHOD_PROPERTY, finalMethod);

            Map<String, List<String>> reqHeaders = CastUtils.cast((Map<?, ?>) message.get(PROTOCOL_HEADERS));
            if (reqHeaders != null)
            {
                for (Map.Entry<String, List<String>> e : reqHeaders.entrySet())
                {
                    String key = e.getKey();
                    String val = format(e.getValue());
                    msg.setOutboundProperty(key, val);
                }
            }
            return msg;
        }));

        if (!Boolean.TRUE.equals(message.containsKey(Message.REQUESTOR_ROLE)))
        {
            message.getInterceptorChain().pause();
        }
    }

    private void extractAndSet(Message message, MutableMuleMessage muleMsg, String cxfHeader, String muleHeader)
    {
        if(message.get(cxfHeader) instanceof Serializable)
        {
            Serializable val = (Serializable) message.get(cxfHeader);
            if (val != null)
            {
                muleMsg.setOutboundProperty(muleHeader, val);
            }
        }
        else
        {
            logger.warn("The header " + cxfHeader + "is not serializable and will not be propagated by Mule");
        }
    }

    private void extractAndSetContentType(Message message, MutableMuleMessage muleMsg)
    {
        String ct = (String) message.get(Message.CONTENT_TYPE);
        if (ct != null)
        {
            String encoding = getEncoding(message);
            if (ct.indexOf("charset") == -1)
            {
                ct = ct + "; charset=" + encoding;
            }
            muleMsg.setOutboundProperty(CONTENT_TYPE, ct);
        }
    }

    private String getEncoding(Message message)
    {
        Exchange ex = message.getExchange();
        String encoding = (String)message.get(Message.ENCODING);
        if (encoding == null && ex.getInMessage() != null) {
            encoding = (String) ex.getInMessage().get(Message.ENCODING);
            message.put(Message.ENCODING, encoding);
        }

        if (encoding == null) {
            encoding = "UTF-8";
            message.put(Message.ENCODING, encoding);
        }
        return encoding;
    }

    private String format(List<String> value)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        
        for (String s : value) {
            if (!first) 
            {
                sb.append(", ");
                first = false;
            }
            else 
            {
                first = false;
            }
            
            sb.append(s);
        }
        return sb.toString();
    }
}


