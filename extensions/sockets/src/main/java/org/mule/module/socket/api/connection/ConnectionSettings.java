/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.socket.api.connection;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.mule.runtime.extension.api.annotation.Parameter;

import java.net.InetSocketAddress;

/**
 * Groups host and port fields.
 * <p>
 * This can be used for representing where a socket should bind in order to listen for connections, or which address
 * should the socket connect to as well.
 *
 * @since 4.0
 */
public class ConnectionSettings
{

    private static final int PORT_CHOSEN_BY_SYSTEM_MASK = 0;

    public ConnectionSettings()
    {
    }

    public ConnectionSettings(Integer port, String host)
    {
        this.port = port;
        this.host = host;
    }

    /**
     * Connection's port number
     */
    @Parameter
    private Integer port;

    /**
     * Connection's host name
     */
    @Parameter
    private String host;

    public Integer getPort()
    {
        return port;
    }

    public String getHost()
    {
        return host;
    }

    /**
     * If the port is {@code null}, let's the system choose a suitable port
     * If the host is {@code null} uses the IP address wildcard address.
     * See {@link InetSocketAddress} for more information
     *
     * @return a valid {@link InetSocketAddress} to be used for binding
     */
    public InetSocketAddress getInetSocketAddress()
    {
        if (isEmpty())
        {
            return new InetSocketAddress(PORT_CHOSEN_BY_SYSTEM_MASK);
        }
        else if (port == null && !isBlank(host))
        {
            return new InetSocketAddress(host, PORT_CHOSEN_BY_SYSTEM_MASK);
        }
        else if (port != null && isBlank(host))
        {
            return new InetSocketAddress(port);
        }

        return new InetSocketAddress(host, port);
    }

    @Override
    public String toString()
    {
        return format("Host %s Port %s", host, port != null ? port.toString() : EMPTY);
    }

    private boolean isEmpty()
    {
        return isBlank(host) && port == null;
    }
}
