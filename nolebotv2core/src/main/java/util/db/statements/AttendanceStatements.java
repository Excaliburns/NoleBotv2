package util.db.statements;

import util.db.DBConnection;
import util.db.entities.AttendanceEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AttendanceStatements {
    static final String insertAttendance =
            " INSERT INTO Attendance                              " +
            "   (DateEntered,       UserID, ServerID, Nickname  ) " +
            "   VALUES                                            " +
            "   (current_timestamp, ?,      ?,        ?         ) ";


    /**
     * Insert the attendance list into the database.
     *
     * @param attendanceList List of attendance entities to add.
     * @return An array of SQL return codes, all should be 0 if everything was ok.
     * @throws SQLException If an error occurs.
     */
    public int[] insertAttendanceList(final List<AttendanceEntity> attendanceList) throws SQLException {
        final PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(insertAttendance);

        for (AttendanceEntity attendanceEntity : attendanceList) {
            preparedStatement.setString(1, attendanceEntity.getUserID());
            preparedStatement.setString(2, attendanceEntity.getServerID());
            preparedStatement.setString(3, attendanceEntity.getNickname());
            preparedStatement.addBatch();
        }

        return preparedStatement.executeBatch();
    }
}
