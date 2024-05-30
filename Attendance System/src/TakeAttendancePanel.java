import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import attendanceManagement.AttendanceRequest;
import attendanceManagement.AttendanceHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TakeAttendancePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton submitButton;
    private String subject; // Subject selected during registration

    public TakeAttendancePanel(String subject) {
        this.subject = subject;
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Take Attendance for " + subject);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Create table model with columns for student ID, name, and attendance status
        tableModel = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class; // Student ID
                if (columnIndex == 1) return String.class; // Student name
                return Boolean.class; // Attendance status (true for present, false for absent)
            }
        };
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Attendance");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Adjust column widths
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(10); // ID column width
        columnModel.getColumn(1).setPreferredWidth(200); // Name column width
        columnModel.getColumn(2).setPreferredWidth(100); // Attendance column width

        submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAttendance();
            }
        });
        add(submitButton, BorderLayout.SOUTH);

        // Load student details into the table
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT id, name FROM " + subject;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                // Add a row to the table model for each student
                tableModel.addRow(new Object[]{id, name, false}); // Initial attendance status is set to false (absent)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load student details.");
        }
    }

    private void submitAttendance() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd"));
            String alterTableQuery = "ALTER TABLE " + subject + " ADD " + currentDate +" BOOLEAN DEFAULT false AFTER email";
            PreparedStatement preparedStatement = connection.prepareStatement(alterTableQuery);
            preparedStatement.executeUpdate();
            System.out.println("New column added to the table successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Attendance already taken for today.");
            return;
        }

        // Process attendance data with handlers
        for (int row = 0; row < table.getRowCount(); row++) {
            int studentId = (int) table.getValueAt(row, 0);
            boolean present = (boolean) table.getValueAt(row, 2);
            AttendanceRequest request = new AttendanceRequest(subject, studentId, present);

            AttendanceHandler presentHandler = new PresentHandler();
            AttendanceHandler absentHandler = new AbsentHandler();
            presentHandler.setNextHandler(absentHandler);
            absentHandler.setNextHandler(null); // End of chain

            presentHandler.handle(request);
        }

        JOptionPane.showMessageDialog(this, "Attendance submitted successfully!");
    }
}
