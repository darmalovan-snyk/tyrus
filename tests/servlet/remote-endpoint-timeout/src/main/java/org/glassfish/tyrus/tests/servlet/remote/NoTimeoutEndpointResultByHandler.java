/*
 * Copyright (c) 2013, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.tyrus.tests.servlet.remote;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.SendHandler;
import jakarta.websocket.SendResult;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

/**
 * @author Stepan Kopriva (stepan.kopriva at oracle.com)
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
@ServerEndpoint(value = "/nobyhandler", configurator = SingletonConfigurator.class)
public class NoTimeoutEndpointResultByHandler {

    private volatile boolean timeoutRaised = false;

    @OnMessage
    public void onMessage(String s, Session session) {
        SendHandler sendHandler = new SendHandler() {
            @Override
            public void onResult(SendResult sendResult) {
                if (!sendResult.isOK()) {
                    Logger.getLogger(NoTimeoutEndpointResultByHandler.class.getName())
                          .log(Level.SEVERE, "Result is not OK.");
                    timeoutRaised = true;
                }
            }
        };

        session.getAsyncRemote().setSendTimeout(1000);
        session.getAsyncRemote().sendText(s, sendHandler);
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    boolean isTimeoutRaised() {
        return timeoutRaised;
    }
}
