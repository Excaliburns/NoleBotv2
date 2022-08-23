import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.tut.nolebotv2core.util.gson.GsonDuration;
import com.tut.nolebotv2core.util.settings.Settings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Collectors;

class SettingsFromJsonTest {
    static Logger logger = LogManager.getLogger(SettingsFromJsonTest.class);
    static Settings testSettings;
    static String sampleSettingsJson;
    static Gson gson;
    static Settings fromJsonSettings;

    @BeforeAll
    public static void setupTestSettings() {
        logger.info("Running SettingsFromJson Test");
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
        InputStream resourceStream = SettingsFromJsonTest.class
                .getClassLoader()
                .getResourceAsStream("samplesettings.json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceStream)));
        sampleSettingsJson = reader.lines().collect(Collectors.joining());
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .registerTypeAdapter(Duration.class, new GsonDuration())
                .create();
        fromJsonSettings = gson.fromJson(sampleSettingsJson, Settings.class);
    }

    @Test
    void loadSettingsFromJsonTest_PrefixCorrect() {
        Assertions.assertEquals(
                fromJsonSettings.getPrefix(),
                testSettings.getPrefix(),
                "Parsed Prefix Equals Test Prefix"
        );
    }

    @Test
    void loadSettingsFromJsonTest_GuildIdCorrect() {
        Assertions.assertEquals(
                fromJsonSettings.getGuildId(),
                testSettings.getGuildId(),
                "Parsed GuildId Equals Test GuildId"
        );
    }

    @Test
    void loadSettingsFromJsonTest_CommandPermissionMapCorrect() {
        Assertions.assertEquals(
                fromJsonSettings.getCommandPermissionMap(),
                testSettings.getCommandPermissionMap(),
                "Parsed Command Permission Map Equals Test Command Permission Map"
        );
    }

    @Test
    void loadSettingsFromJsonTest_PermissionListCorrect() {
        Assertions.assertEquals(
                fromJsonSettings.getPermissionList(),
                testSettings.getPermissionList(),
                "Parsed Permission List Equals Test Permission List"
        );
    }
}
