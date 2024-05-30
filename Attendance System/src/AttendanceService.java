import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import attendanceManagement.AttendanceService;

class AttendanceServiceImpl implements AttendanceService {

    @Override
    public void loadAttendanceDetails(DefaultTableModel tableModel, String date, String email) {
        tableModel.setRowCount(0); // Clear existing data
        Connection connection = DatabaseConnection.getInstance().getConnection();

        String[] subjects = {"dsp", "prj", "uiux", "adl"};
        String[] subjectNames = {"DSP", "PRJ", "UIUX", "ADL"};

        for (int i = 0; i < subjects.length; i++) {
            String subject = subjects[i];
            String subjectName = subjectNames[i];
            String query = "SELECT '" + subjectName + "' AS subject, " + date + " AS attendance FROM " + subject + " WHERE email = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String resultSubject = resultSet.getString("subject");
                    boolean attendance = resultSet.getBoolean("attendance");
                    String attendanceStatus = attendance ? "Present" : "Absent";
                    tableModel.addRow(new Object[]{resultSubject, attendanceStatus});
                }
            } catch (SQLException e) {
                if (e.getMessage().contains("Unknown column")) {
                    System.out.println("No attendance taken for " + subjectName + " on the given date.");
                } else {
                    e.printStackTrace();
                }
            }
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No attendance taken for the given date.");
        }
    }
}
