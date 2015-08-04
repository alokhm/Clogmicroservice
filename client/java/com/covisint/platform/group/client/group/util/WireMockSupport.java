/* Copyright (C) 2015 Covisint. All Rights Reserved. */

package com.covisint.platform.group.client.group.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Wire mock support to find a free port.
 */
public final class WireMockSupport {

    /** Private constructor. */
    private WireMockSupport() {

    }

    /**
     * Will return a port number that is automatically allocated.
     * 
     * @return Returns free port.
     */
    public static int findFreePort() {
        try {
            final ServerSocket socket = new ServerSocket(0);
            final int result = socket.getLocalPort();
            socket.close();

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
