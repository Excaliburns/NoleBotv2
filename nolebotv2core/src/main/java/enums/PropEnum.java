package enums;

import java.util.HashMap;

public enum PropEnum {
    // TOKEN ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    TOKEN            ("bot.token");

    private final String propertyKey;

    PropEnum(String propertyKey) {
        this.propertyKey = propertyKey;
        getMappings().put(propertyKey, this);
    }


    private static HashMap<String, PropEnum> mappings;
    private static HashMap<String, PropEnum> getMappings() {
        if (mappings == null) {
            synchronized (PropEnum.class) {
                if (mappings == null) {
                    mappings = new HashMap<>();
                }
            }
        }

        return mappings;
    }

    public static PropEnum forValue(String value) {
        return getMappings().get(value);
    }
}
