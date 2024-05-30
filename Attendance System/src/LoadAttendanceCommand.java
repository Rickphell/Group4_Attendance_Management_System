import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import attendanceManagement.Command;

public class LoadAttendanceCommand implements Command {
    private EditAttendancePanel panel;

    public LoadAttendanceCommand(EditAttendancePanel panel) {
        this.panel = panel;
    }

    @Override
    public void execute() {
        panel.tableModel.setRowCount(0); // Clear the table
        List<Student> students = new ArrayList<>();

        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String date = panel.getCurrentDateField();
            String query = "SELECT id, name," + date + " AS attendance FROM " + panel.subject;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                boolean present = resultSet.getBoolean("attendance");
                students.add(new Student(id, name, present));
                panel.tableModel.addRow(new Object[]{id, name, present});
            }

            panel.students = students.toArray(new Student[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(panel, "No attendance found.");
        }
    }
}
