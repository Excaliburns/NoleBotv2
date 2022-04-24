package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.gson.GsonDuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Collectors;

public class FilesUtil {
    private static final Logger logger = LogManager.getLogger(FilesUtil.class);
    public static final Gson GSON_INSTANCE = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new GsonDuration())
            .create();

    /**
     * Create file and directories if they don't already exist.
     *
     * @param file URI
     */
    public static void createFileIfNotExists(Path file) {
        if (!Files.exists(file.getParent())) {
            logger.warn("Properties directory did not exist. Creating from default.");

            try {
                Files.createDirectory(file.getParent());
            }
            catch (IOException e) {
                logger.error("Could not create config directory. {}", e::getMessage);
            }
        }

        if (!Files.exists(file)) {
            logger.warn("Properties file did not exist. Creating from default.");

            try {
                Files.createFile(file);
            }
            catch (IOException e) {
                logger.error("Could not create config file. {}", e::getMessage);
            }
        }
    }

    /**
     * Creates file and returns file contents as string (will be empty in most cases).
     *
     * @param uri URI for file
     * @return Contents of file
     */
    public static String getFileContentsAsStringAndCreateIfNotExists(final Path uri) {
        createFileIfNotExists(uri);
        return getFileContentsAsString(uri);
    }

    /**
     * Gets String content of file.
     *
     * @param uri URI for file
     * @return Contents of file
     */
    public static String getFileContentsAsString(final Path uri) {
        String fileString;
        logger.info("Getting file as string: {}", () -> uri);

        try {
            BufferedReader reader = Files.newBufferedReader(uri);
            fileString = reader.lines().collect(Collectors.joining());
        }
        catch (IOException e) {
            logger.error("Exception occurred while reading file: {}\nReturning empty string.", e::getMessage);
            return "";
        }

        return fileString;
    }

    /**
     * Creates file and writes string to it.
     *
     * @param uri URI
     * @param fileContents String to write to file
     */
    public static void writeStringToFileAndCreateIfNotExists(final Path uri, final String fileContents) {
        createFileIfNotExists(uri);
        writeStringToFile(uri, fileContents);
    }

    /**
     * Writes string to file.
     *
     * @param uri URI
     * @param fileContents String to write to file
     */
    public static void writeStringToFile(final Path uri, final String fileContents) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(uri);

            writer.write(fileContents);
            writer.close();
        }
        catch (IOException e) {
            logger.error("Could not create writer for file {}, Reason: {}",
                    uri::toString,
                    e::getMessage
            );
        }
    }
}
