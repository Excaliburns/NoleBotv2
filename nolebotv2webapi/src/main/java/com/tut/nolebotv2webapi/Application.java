package com.tut.nolebotv2webapi;

import com.tut.nolebotv2webapi.coreconnect.CoreWebSocketConnector;
import io.micronaut.runtime.Micronaut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

public class Application {
    private static final Logger logger = LogManager.getLogger();
    public static WebSocketClient coreClient;

    public static void main(String[] args) throws URISyntaxException {
        Micronaut.run(Application.class, args);

        if (getWebsocketEnabled()) {
            coreClient = new CoreWebSocketConnector(new URI("ws://localhost:13037"));
            coreClient.connect();
        }
    }

    private static boolean getWebsocketEnabled() {
        final Properties properties = getProperties();
        return properties != null && Boolean.parseBoolean(properties.getProperty("websocket.enabled"));
    }

    private static Properties getProperties() {
        final Properties properties = new Properties();
        final String propFileName = "config.properties";

        final InputStream inputStream = Application.class.getClassLoader().getResourceAsStream(propFileName);
        try {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            return null;
        }
    }
}
