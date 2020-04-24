/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.test.e2e.jdk8;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;
import org.glassfish.tyrus.test.tools.TestContainer;

import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class DefaultMethodTest extends TestContainer {

    public static interface EchoInterface {

        @OnMessage
        public default void echo(Session session, String message) throws IOException {
            session.getBasicRemote().sendText(message + " (from your server)");
            session.close();
        }
    }

    @ServerEndpoint("/defaultMethodTestEcho")
    public static class EchoEndpoint implements EchoInterface {

        @OnError
        public void onError(Throwable t) {
            t.printStackTrace();
        }
    }

    @Test
    public void defaultMethodTest() throws DeploymentException, InterruptedException, IOException {
        final Server server = startServer(EchoEndpoint.class);

        final CountDownLatch messageLatch = new CountDownLatch(1);
        final CountDownLatch onCloseLatch = new CountDownLatch(1);

        try {
            final ClientManager client = createClient();
            client.connectToServer(new Endpoint() {
                @Override
                public void onOpen(Session session, EndpointConfig EndpointConfig) {

                    try {
                        session.addMessageHandler(String.class, message -> {
                            System.out.println("### Received: " + message);

                            if (message.equals("Do or do not, there is no try. (from your server)")) {
                                messageLatch.countDown();
                            }
                        });

                        session.getBasicRemote().sendText("Do or do not, there is no try.");
                    } catch (IOException e) {
                        // do nothing
                    }
                }

                @Override
                public void onClose(Session session, CloseReason closeReason) {
                    System.out.println("### Client session closed: " + closeReason);
                    onCloseLatch.countDown();
                }

            }, ClientEndpointConfig.Builder.create().build(), getURI(EchoEndpoint.class));

            assertTrue(messageLatch.await(1, TimeUnit.SECONDS));
            assertTrue(onCloseLatch.await(1, TimeUnit.SECONDS));

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            stopServer(server);
        }
    }

}
