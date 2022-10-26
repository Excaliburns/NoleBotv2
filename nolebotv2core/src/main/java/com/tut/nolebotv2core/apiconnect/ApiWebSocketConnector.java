package com.tut.nolebotv2core.apiconnect;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.enums.BroadcastType;
import com.tut.nolebotshared.enums.MessageType;
import com.tut.nolebotv2core.enums.PropEnum;
import com.tut.nolebotv2core.util.PropertiesUtil;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.jetbrains.annotations.NotNull;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * This class handles incoming connections from the Nolebot Webserver.
 */
@ClientEndpoint
public class ApiWebSocketConnector {
    private static final Logger logger = LogManager.getLogger(ApiWebSocketConnector.class);
    private static final ScheduledExecutorService executorService  = Executors.newScheduledThreadPool(4,
        new ThreadFactory() {
            private static int num = 0;
            @Override
            public Thread newThread(@NotNull Runnable r) {
                num++;
                Thread t = Executors.defaultThreadFactory().newThread(r);
                t.setName("API-WS-" + (num - 1));
                return t;
            }
        });
    Session userSession;
    private MessageHandler messageHandler;

    /**
     * Constructor.
     *
     * @param endpointURI Where to connect
     */
    public ApiWebSocketConnector(final URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
            executorService.scheduleAtFixedRate(() -> {
                final UUID correlationId = UUID.randomUUID();
                try {
                    sendMessage(
                            BroadcastPackage.builder()
                                    .messageType(MessageType.REQUEST)
                                    .broadcastType(BroadcastType.HEARTBEAT)
                                    .correlationId(correlationId)
                                    .build()
                    ).get();
                }
                catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }, 0, 10, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        logger.info("Opened WS connection to: {}", () -> userSession.getRequestURI().toString());
        this.userSession = userSession;
    }

    /**
     * Listener for SocketClose event, tries to reconnect.
     *
     * @param userSession Current WS session
     * @throws ExecutionException   if the future completed exceptionally
     * @throws InterruptedException if the future was interrupted
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) throws ExecutionException, InterruptedException {
        logger.info("Closed WS connection to: {}", () -> userSession.getRequestURI().toString());
        logger.info("Reason: {}", reason::toString);

        this.userSession = null;
    }

    @OnMessage
    public void onMessage(byte[] message) {
        messageHandler.handleMessage((BroadcastPackage) SerializationUtils.deserialize(message));
    }

    /**
     * Sends a message to the server.
     *
     * @param broadcastPackage The package to send
     */
    public Future<Boolean> sendMessage(BroadcastPackage broadcastPackage) {
        broadcastPackage.setMessageType(MessageType.RESPONSE);
        byte[] arr = SerializationUtils.serialize(broadcastPackage);
        if (broadcastPackage.getBroadcastType() == BroadcastType.HEARTBEAT) {
            logger.debug(
                    "WEBSOCKET MESSAGE: [MessageType: {}, BroadcastType: {}, CorrelationId: {}, size: {}]",
                    broadcastPackage::getMessageType,
                    broadcastPackage::getBroadcastType,
                    broadcastPackage::getCorrelationId,
                    (Supplier<Integer>) () -> arr.length
            );
        }
        else {
            logger.trace("Heartbeat received");
        }

        return executorService.submit(() -> {
            try {
                this.userSession.getBasicRemote().sendBinary(ByteBuffer.wrap(arr));
                return true;
            }
            catch (IOException e) {
                logger.catching(e);
                return false;
            }
        });
    }

    public void addMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Message handler.
     */
    public interface MessageHandler {
        void handleMessage(BroadcastPackage message);
    }

    /**
     * Attempts to connect to nolebotv2webapi.
     *
     * @return A future that gives the WS connector
     */
    public static CompletableFuture<ApiWebSocketConnector> tryConnectApi() {
        final String socketPath = "ws://" + PropertiesUtil.getProperty(PropEnum.API_WEBSOCKET_ENDPOINT) +
                "/internalApi/" + PropertiesUtil.getProperty(PropEnum.API_WEBSOCKET_SECRET);
        CompletableFuture<ApiWebSocketConnector> connectionFuture = CompletableFuture.supplyAsync(() -> {
            ApiWebSocketConnector c = null;
            while (c == null) {
                logger.debug("Trying to connect to {}", socketPath);
                try {
                    c = new ApiWebSocketConnector(URI.create(socketPath));
                }
                catch (Exception e) {
                    logger.error("{}", e::getMessage);
                    logger.info("Swallowing exception, will attempt to reconnect api in 10 seconds.");
                    try {
                        Thread.sleep(10000);
                    }
                    catch (InterruptedException ex) {
                        logger.catching(ex);
                    }
                }
            }
            return c;
        }, executorService);

        return connectionFuture;
    }
}