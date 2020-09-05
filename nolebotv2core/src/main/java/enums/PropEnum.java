package enums;

import java.util.HashMap;

public enum PropEnum {
    // TOKEN ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    TOKEN            ("bot.token");

    private final String propertyKey;
    public static final String AVATAR = "https://cdn.discordapp.com/avatars/548200687964520459/ab398fa8cce3195682257ff692b53ecb.webp?size=128";
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
