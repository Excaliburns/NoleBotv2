package apiconnect;

import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.enums.MessageType;
import enums.PropEnum;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.PropertiesUtil;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This class handles incoming connections from the Nolebot Webserver.
 */
@ClientEndpoint
public class ApiWebSocketConnector {
    private static final Logger logger = LogManager.getLogger(ApiWebSocketConnector.class);
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
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
     * @throws ExecutionException if the future completed exceptionally
     * @throws InterruptedException if the future was interrupted
     */
    @OnClose
    public void onClose(Session userSession) throws ExecutionException, InterruptedException {
        logger.info("Closed WS connection to: {}", () -> userSession.getRequestURI().toString());
        this.userSession = null;
        try {
            tryConnectApi().get(60, TimeUnit.SECONDS);
        }
        catch (TimeoutException e) {
            logger.error("Couldn't reopen WS connection");
        }
    }

    @OnMessage
    public void onMessage(byte[] message) {
        messageHandler.handleMessage((BroadcastPackage) SerializationUtils.deserialize(message));
    }

    public void sendMessage(BroadcastPackage broadcastPackage) {
        broadcastPackage.setMessageType(MessageType.RESPONSE);
        this.userSession.getAsyncRemote().sendBinary(ByteBuffer.wrap(SerializationUtils.serialize(broadcastPackage)));
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
     * Attempts to connect to nolebotwv2webapi
     *
     * @return A future that gives the WS connector
     */
    public static CompletableFuture<ApiWebSocketConnector> tryConnectApi() {
        CompletableFuture<ApiWebSocketConnector> completableFuture = new CompletableFuture<>();
        final ScheduledFuture<?> checkFuture = executorService.scheduleAtFixedRate(() -> {
            try {
                final ApiWebSocketConnector connector = new ApiWebSocketConnector(URI.create(
                        "ws://localhost:8080/internalApi/" + PropertiesUtil.getProperty(
                                PropEnum.API_WEBSOCKET_SECRET
                        )
                ));
                completableFuture.complete(connector);
            }
            catch (Exception e) {
                logger.info("Swallowing exception, will attempt to reconnect api in 10 seconds.");
            }
        }, 0, 10, TimeUnit.SECONDS);
        completableFuture.whenComplete((result, thrown) -> checkFuture.cancel(true));
        return completableFuture;
    }
}