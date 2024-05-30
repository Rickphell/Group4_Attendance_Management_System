import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import attendanceManagement.Command;

import javax.swing.JOptionPane;

// concrete command
public class SaveAttendanceCommand implements Command {
    private EditAttendancePanel panel;
    private Student[] students;
    private List<Student> studentList;

    public SaveAttendanceCommand(EditAttendancePanel panel, Student[] students) {
        this.panel = panel;
        this.students = students;
    }

    public SaveAttendanceCommand(EditAttendancePanel panel, List<Student> students) {
        this.panel = panel;
        this.studentList = students;
    }

    @Override
    public void execute() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            List<Boolean> attendanceStatus = new ArrayList<>();

            // Iterate through the table to collect attendance data
            for (int row = 0; row < panel.tableModel.getRowCount(); row++) {
                boolean present = (boolean) panel.tableModel.getValueAt(row, 2);
                attendanceStatus.add(present);
            }
            System.out.println(attendanceStatus);
            System.out.println("Saving attendance...");

            if (students != null) {
                int i = 0;
                for (Student student : students) {
                    String query = "UPDATE " + panel.subject + " SET " + panel.getCurrentDateField() + " = ? WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setBoolean(1, attendanceStatus.get(i));
                    preparedStatement.setInt(2, student.getId());
                    int rowsUpdated = preparedStatement.executeUpdate();
                    System.out.println("Updated " + rowsUpdated + " row(s) for student " + student.getId());
                    System.out.println("status "+attendanceStatus.get(i));
                    i++;
                }
            } else if (studentList != null) {
                for (Student student : studentList) {
                    String query = "UPDATE " + panel.subject + " SET " + panel.getCurrentDateField() + " = ? WHERE id = ?";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setBoolean(1, student.isPresent());
                    preparedStatement.setInt(2, student.getId());
                    int rowsUpdated = preparedStatement.executeUpdate();
                    System.out.println("Updated " + rowsUpdated + " row(s) for student " + student.getId());
                }
            }
            JOptionPane.showMessageDialog(panel, "Attendance updated successfully.");
            System.out.println("Attendance saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "Failed to update attendance.");
        }
    }
}
