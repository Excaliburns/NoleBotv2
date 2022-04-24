package util;

import enums.PropEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
    private static final Logger logger = LogManager.getLogger(PropertiesUtil.class);
    //
    private static final HashMap<PropEnum, String> propertyMap;

    /*
     * Checks for property directory and file. If not found, NoleBot will create both.
     */
    static {
        logger.info("Loading properties..");
        propertyMap = new HashMap<>();
        final String DEFAULT_FILE_PATH = "config/";
        final String DEFAULT_FILE_NAME = "config.properties";

        final Path defaultFile     = Paths.get(DEFAULT_FILE_PATH + DEFAULT_FILE_NAME);

        FilesUtil.createFileIfNotExists(defaultFile);

        BufferedInputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(Files.newInputStream(defaultFile));
        }
        catch (IOException e) {
            logger.error("Could not create Input Stream from properties file. {}", e.getMessage());
        }

        if (inputStream != null) {
            final Properties properties = new Properties();

            try {
                properties.load(inputStream);

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    final String propertyKey   = (String) entry.getKey();
                    final String propertyValue = (String) entry.getValue();

                    PropEnum enumValue = PropEnum.forValue(propertyKey);

                    propertyMap.put(enumValue, propertyValue);
                }

                logger.info("Successfully loaded all properties.");
            }
            catch (IOException e) {
                logger.error("Could not load Input Stream from property file into Properties. {}", e.getMessage());
            }
        }
    }

    public static String getProperty(PropEnum propertyName) {
        return propertyMap.get(propertyName);
    }


    public static HashMap<PropEnum, String> getPropertyMap() {
        return propertyMap;
    }
}
