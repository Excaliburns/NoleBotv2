package util.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class Attendance {
    private String userID;
    private String serverID;
    private String nickname;
}
