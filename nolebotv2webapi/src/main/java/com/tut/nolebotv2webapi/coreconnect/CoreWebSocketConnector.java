package com.tut.nolebotv2webapi.coreconnect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.ConnectException;
import java.net.URI;
import java.nio.ByteBuffer;

public class CoreWebSocketConnector extends WebSocketClient {
    private static final Logger logger = LogManager.getLogger();
    private final WebSocketHandler handler = new WebSocketHandler();
    /**
     * Constructs a WebSocketClient instance and sets it to the connect to the
     * specified URI. The channel does not attampt to connect automatically. The connection
     * will be established once you call <var>connect</var>.
     *
     * @param serverUri the server URI to connect to
     */
    public CoreWebSocketConnector(URI serverUri) {
        super(serverUri);
    }

    /**
     * Called after an opening handshake has been performed and the given websocket is ready to be written on.
     *
     * @param handshakedata The handshake of the websocket instance
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        logger.info("API websocket client connection opened");
    }

    @Override
    public void onMessage(String message) {
        logger.info(String.format("Received message from server [%s] : %s", this.getConnection().getLocalSocketAddress(), message));
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
    }

    /**
     * Called after the websocket connection has been closed.
     *
     * @param code   The codes can be looked up here: {@link CloseFrame}
     * @param reason Additional information string
     * @param remote
     **/
    @Override
    public void onClose(int code, String reason, boolean remote) {
        logger.info("API websocket client connection closed");
    }

    /**
     * Called when errors occurs. If an error causes the websocket connection to fail {@link #onClose(int, String, boolean)} will be called additionally.<br>
     * This method will be called primarily because of IO or protocol errors.<br>
     * If the given exception is an RuntimeException that probably means that you encountered a bug.<br>
     *
     * @param ex The exception causing this error
     **/
    @Override
    public void onError(Exception ex) {
        logger.info("API websocket client connection error: {}", ex.getMessage());
    }
}
