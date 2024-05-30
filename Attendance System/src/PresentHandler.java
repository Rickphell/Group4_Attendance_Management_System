import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import attendanceManagement.AttendanceRequest;
import attendanceManagement.AttendanceHandler;

public class PresentHandler implements AttendanceHandler {
    private AttendanceHandler nextHandler;

    @Override
    public void setNextHandler(AttendanceHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(AttendanceRequest request) {
        if (request.isStatus()) { // Process only if the student is present
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
                String updateQuery = "UPDATE " + request.getSubject() + " SET " + currentDate + " = ? WHERE id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                preparedStatement.setBoolean(1, true); // Set the attendance status to present
                preparedStatement.setInt(2, request.getStudentId()); // Set the student ID
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to update present status for student ID: " + request.getStudentId());
            }
        }
        
        if (nextHandler != null) {
            nextHandler.handle(request);
        }
    }
}
