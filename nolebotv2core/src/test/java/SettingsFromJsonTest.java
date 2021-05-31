import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.settings.Settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;


public class SettingsFromJsonTest {
    static Logger logger = LogManager.getLogger(SettingsFromJsonTest.class);
    Settings testSettings;

    @BeforeEach
    public void setupTestSettings() {
        testSettings = new Settings();

        testSettings.setPrefix("!");
        testSettings.setGuildId("138481681630887936");
        testSettings.setPermissionList(new TreeSet<>());

        HashMap<String, Integer> testCommandPermissionMap = new HashMap<>(Map.of(
                "help", 1000,
                "ping", 200,
                "tut", 0
        ));


        testSettings.setCommandPermissionMap(testCommandPermissionMap);
    }

    @Test
    public void LoadSettingsFromJsonTest() {
        logger.info("Running SettingsFromJson Test");
        InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("samplesettings.json");

        assertNotNull(resourceStream);

        BufferedReader reader = new BufferedReader( new InputStreamReader(resourceStream));

        assertNotNull(reader);
        logger.info("Read settings successfully");

        String sampleSettingsJson = reader.lines().collect(Collectors.joining());
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();

        Settings settings = gson.fromJson(sampleSettingsJson, Settings.class);

        assertEquals(settings.getPrefix              (), testSettings.getPrefix());
        assertEquals(settings.getGuildId             (), testSettings.getGuildId());
        assertEquals(settings.getCommandPermissionMap(), testSettings.getCommandPermissionMap());
        assertEquals(settings.getPermissionList      (), testSettings.getPermissionList());

        logger.info("Completed SettingsFromJson Test");
    }
}
