package com.tut.nolebotv2webapi.coreconnect;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnError;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import jakarta.inject.Singleton;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@ServerWebSocket("/internalApi/{clientSecret}")
@Secured(SecurityRule.IS_ANONYMOUS)
@Singleton
public class CoreWebSocketServer {
    private static final Logger logger = LogManager.getLogger(CoreWebSocketServer.class);
    private final String secret;
    private final WebSocketBroadcaster broadcaster;

    Map<UUID, CompletableFuture<BroadcastPackage>> requests;

    /**
     * Default constructor.
     *
     * @param secret Secret of the websocket server.
     * @param broadcaster Message broadcaster.
     */
    public CoreWebSocketServer(
            @Property(name = "nolebot.websocket.secret") String secret,
            WebSocketBroadcaster broadcaster
    ) {
        this.secret = secret;
        this.broadcaster = broadcaster;
        requests = new ConcurrentHashMap<>();
    }

    /**
     * Sends a message to the client without expecting a response.
     *
     * @param broadcastPackage The package to send
     */
    public void send(final BroadcastPackage broadcastPackage) {
        try {
            broadcaster.broadcastSync(
                    SerializationUtils.serialize(broadcastPackage), MediaType.MULTIPART_FORM_DATA_TYPE)
            ;
        }
        catch (Exception e) {
            logger.error("Error sending data through WebSocket: {}", e::getMessage);
        }
    }

    /**
     * Send a message to the client and expect a response back.
     *
     * @param broadcastPackage Package of information to broadcast
     * @return a broadcast package containing response information
     * @throws ExecutionException if future throws an exception
     * @throws InterruptedException if future's thread is interrupted
     * @throws TimeoutException if future is not completed within 60000ms
     */
    public BroadcastPackage sendWithResponse(final BroadcastPackage broadcastPackage)
            throws ExecutionException, InterruptedException, TimeoutException {
        broadcastPackage.setMessageType(MessageType.REQUEST);

        final UUID correlationId = UUID.randomUUID();
        broadcastPackage.setCorrelationId(correlationId);

        final CompletableFuture<BroadcastPackage> future = new CompletableFuture<>();
        requests.put(correlationId, future);

        this.send(broadcastPackage);

        return future.get(60000, TimeUnit.MILLISECONDS);
    }

    /**
     * What happens when a connection is opened.
     * If the path does not contain a matching secret, the session is closed.
     *
     * @param session Session that was just opened.
     * @param clientSecret Secret that was sent by client.
     */
    @OnOpen
    public void onOpen(final WebSocketSession session, final @PathVariable String clientSecret) {
        if (!Objects.equals(clientSecret, this.secret)) {
            session.close(CloseReason.POLICY_VIOLATION);
        }
        else {
            BroadcastPackage ack = BroadcastPackage.builder().broadcastType(BroadcastType.ACK)
                    .messageType(MessageType.REQUEST).payload("Successfully established WS connection").build();
            broadcaster.broadcastSync(SerializationUtils.serialize(ack), MediaType.MULTIPART_FORM_DATA_TYPE);
            logger.info("Established a WS connection: {}", session::getRequestURI);
        }
    }

    /**
     * What happens when a message is sent from the client.
     *
     * @param message a bytearray containing the message data, will be cast to a BroadcastPackage
     * @param session The session between the server and the client.
     * @param clientSecret Secret that was sent by client.
     */
    @OnMessage
    public void onMessage(byte[] message, WebSocketSession session, @PathVariable String clientSecret) {
        if (!Objects.equals(clientSecret, this.secret)) {
            session.close(CloseReason.POLICY_VIOLATION);
        }

        final BroadcastPackage broadcastPackage = (BroadcastPackage) SerializationUtils.deserialize(message);
        final Consumer<? super CompletableFuture<BroadcastPackage>> packageCompletableFuture =
                broadcastPackageCompletableFuture -> broadcastPackageCompletableFuture.complete(broadcastPackage);

        switch (broadcastPackage.getMessageType()) {
            case RESPONSE: {
                logger.debug(
                        "Message - RESPONSE: [BroadcastType: {}, CorrelationId: {}] ",
                        broadcastPackage::getBroadcastType,
                        broadcastPackage::getCorrelationId
                );
                Optional<CompletableFuture<BroadcastPackage>> correlatingOutstandingMessage = Optional.ofNullable(
                        this.requests.remove(broadcastPackage.getCorrelationId())
                );

                correlatingOutstandingMessage.ifPresent(packageCompletableFuture);
                break;
            }
            case REQUEST: {
                logger.debug(
                        "Message - REQUEST: [BroadcastType: {}, CorrelationId: {}] ",
                        broadcastPackage::getBroadcastType,
                        broadcastPackage::getCorrelationId
                );
            }
            default: {
                logger.warn(
                        "Message broadcast type {} did not have matching case",
                        broadcastPackage::getMessageType
                );
            }
        }
    }

    @OnClose
    public void onClose(WebSocketSession session, @PathVariable String clientSecret) {
        logger.info("WS connection: {} has disconnected.", session::getRequestURI);
    }

    @OnError
    public void onError(WebSocketSession session, Throwable t) {
        logger.error("WS errored out with error {}", t::getMessage);
    }
}
