//CHECKSTYLE:OFF
package enums;

import java.util.HashMap;
import java.util.Map;

public enum PropEnum {
    // TOKEN ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    BOT_AVATAR_URL               ("bot.avatar.url"),
    DB_NAME                      ("db.name"),
    DB_ADDR                      ("db.addr"),
    DB_USER                      ("db.user"),
    DB_PASS                      ("db.pass"),
    DB_PORT                      ("db.port"),
    DB_TYPE                      ("db.type"),
    TOKEN                        ("bot.token"),
    API_WEBSOCKET_ENABLED        ("api.websocket.enabled"),
    API_WEBSOCKET_SECRET         ("api.websocket.secret"),
    ATTENDANCE_LEADER_IN_CHANNEL ("bot.attendance.leader.in.channel");

    private final String propertyKey;

    PropEnum(String propertyKey) {
        this.propertyKey = propertyKey;
        getMappings().put(propertyKey, this);
    }

    private static Map<String, PropEnum> mappings;
    @SuppressWarnings("DoubleCheckedLocking")
    private static Map<String, PropEnum> getMappings() {
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
