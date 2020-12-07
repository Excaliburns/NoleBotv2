package util.db.entities;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
public class Attendance {
    private Timestamp dateEntered;
    private String userID;
    private String serverID;
    private String discordTag;
}
