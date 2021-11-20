package com.tut.nolebotv2webapi.coreconnect;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@ServerWebSocket("/internalApi/{clientSecret}")
public class CoreWebSocketServer {
    private static final Logger logger = LogManager.getLogger(CoreWebSocketServer.class);
    private final String secret;

    @Getter
    private final WebSocketBroadcaster broadcaster;

    public CoreWebSocketServer(
            @Property( name = "nolebot.websocket.secret") String secret,
            WebSocketBroadcaster broadcaster
    ) {
        this.secret = secret;
        this.broadcaster = broadcaster;
    }

    @OnOpen
    public void onOpen(WebSocketSession session, @PathVariable String clientSecret) {
        if (!Objects.equals(clientSecret, this.secret)) {
            session.close(CloseReason.POLICY_VIOLATION);
        }
        else {
            logger.info("Established a WS connection: " + session.getRequestURI());
        }
    }

    @OnMessage
    public void onMessage(String message, WebSocketSession session, @PathVariable String clientSecret) {
        if (!Objects.equals(clientSecret, this.secret)) {
            session.close(CloseReason.POLICY_VIOLATION);
        }

        System.out.println("message");
    }

    @OnClose
    public void onClose(WebSocketSession session, @PathVariable String clientSecret) {
        logger.info("WS connection: " + session.getRequestURI() + " has disconnected.");
    }
}
