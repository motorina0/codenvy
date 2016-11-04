package com.codenvy.ext.java.server;

import org.eclipse.che.api.core.websocket.impl.BasicWebSocketEndpoint;
import org.eclipse.che.api.core.websocket.impl.GuiceInjectorEndpointConfigurator;
import org.eclipse.che.api.core.websocket.impl.PendingMessagesReSender;
import org.eclipse.che.api.core.websocket.impl.WebSocketSessionRegistry;
import org.eclipse.che.api.core.websocket.impl.WebSocketTransmissionDispatcher;

import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;

/**
 * @author Vitalii Parfonov
 */

@ServerEndpoint(value = "/{node.port_host}/websocket/{endpoint-id}", configurator = GuiceInjectorEndpointConfigurator.class)
public class CodenvyWebSocketEndpoint extends BasicWebSocketEndpoint {

    @Inject
    public CodenvyWebSocketEndpoint(WebSocketSessionRegistry registry,
                                    PendingMessagesReSender reSender,
                                    WebSocketTransmissionDispatcher dispatcher) {
        super(registry, reSender, dispatcher);
    }
}
