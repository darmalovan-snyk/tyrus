/*
 * Copyright (c) 2012, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.tyrus.core;

import jakarta.websocket.MessageHandler;

/**
 * Asynchonous message handler with added getType method.
 *
 * @author Martin Matula (martin.matula at oracle.com)
 */
interface AsyncMessageHandler extends MessageHandler.Partial {

    /**
     * Get type of handled message.
     *
     * @return type of handled message.
     */
    Class<?> getType();

    /**
     * Get max message size allowed for this message handler.
     *
     * @return max message size.
     * @see jakarta.websocket.OnMessage#maxMessageSize()
     */
    long getMaxMessageSize();
}
