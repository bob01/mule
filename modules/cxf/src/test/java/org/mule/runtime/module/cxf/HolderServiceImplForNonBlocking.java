/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.cxf;

import javax.jws.WebService;
import javax.xml.ws.Holder;

@WebService(endpointInterface = "org.mule.runtime.module.cxf.HolderService")
public class HolderServiceImplForNonBlocking implements HolderService
{
    public String echo(String s1, Holder<String> outS1, Holder<String> outS2)
    {
        delay();
        outS1.value = s1 + "-holder1";
        outS2.value = s1 + "-holder2";
        return s1 + "-response";
    }

    public String echo2(String s1, Holder<String> outS1, String s2)
    {
        delay();
        outS1.value = s2 + "-holder";
        return s1 + "-response";
    }

    public void echo3(Holder<String> outS1, String s1)
    {
        delay();
        outS1.value = s1;
    }

    private void delay()
    {
        try
        {
            Thread.sleep(1000L);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
