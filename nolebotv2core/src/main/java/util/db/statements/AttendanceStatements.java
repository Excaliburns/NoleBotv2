package util.db.statements;

import util.db.DBConnection;
import util.db.entities.Attendance;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AttendanceStatements {
    final String INSERT_ATTENDANCE =
    " INSERT INTO Attendance                              " +
    "   (DateEntered,       UserID, ServerID, Nickname  ) " +
    "   VALUES                                            " +
    "   (current_timestamp, ?,      ?,        ?         ) " ;


    public int[] insertAttendanceList(final List<Attendance> attendanceList) throws SQLException {
        final PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(INSERT_ATTENDANCE);

        for (Attendance attendance : attendanceList) {
            preparedStatement.setString(1, attendance.getUserID());
            preparedStatement.setString(2, attendance.getServerID());
            preparedStatement.setString(3, attendance.getNickname());
            preparedStatement.addBatch();
        }

        return preparedStatement.executeBatch();
    }
}
