package apiconnect;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.tut.nolebotshared.entities.BroadcastPackage;
import com.tut.nolebotshared.enums.MessageType;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * This class handles incoming connections from the Nolebot Webserver.
 */
@ClientEndpoint
public class ApiWebSocketConnector {
    private static final Logger logger = LogManager.getLogger(ApiWebSocketConnector.class);

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

    @OnClose
    public void onClose(Session userSession) {
        logger.info("Closed WS connection to: {}", () -> userSession.getRequestURI().toString());
        this.userSession = null;
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
}