package apiconnect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

@ClientEndpoint
public class ApiWebSocketConnector {
    private static final Logger logger = LogManager.getLogger(ApiWebSocketConnector.class);

    Session userSession;
    private MessageHandler messageHandler;

    public ApiWebSocketConnector(final URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @OnOpen
    public void onOpen(Session userSession) {
        logger.info("Opened WS connection to: {}", userSession.getRequestURI().toString());
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession) {
        logger.info("Closed WS connection to: {}", userSession.getRequestURI().toString());
        this.userSession = null;
    }

    @OnMessage
    public void onMessage(byte[] message) {
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    @OnMessage
    public void onMessage(String message) {
        messageHandler.handleMessage(message);
    }

    public void addMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * Message handler.
     *
     * @author Jiji_Sasidharan
     */
    public interface MessageHandler {
        void handleMessage(String message);
    }
}