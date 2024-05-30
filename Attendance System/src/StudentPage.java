import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.sql.*;
import attendanceManagement.AttendanceService;

public class StudentPage extends JFrame {
    private JLabel emailLabel;
    private JTextField dateField;
    private JButton fetchButton;
    private JTable table;
    private DefaultTableModel tableModel;
    private AttendanceService attendanceService;
    private JTextArea notificationsArea;
    private String email;

    public StudentPage(String email) {
        this.email = email;
        setTitle("Student Page");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        emailLabel = new JLabel("Email: " + email);
        topPanel.add(emailLabel, BorderLayout.NORTH);
        JPanel datePanel = new JPanel();
        datePanel.add(new JLabel("Enter Date: "));
        dateField = new JTextField(10);
        datePanel.add(dateField);
        fetchButton = new JButton("Fetch Attendance");
        fetchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fetchAttendance();
            }
        });
        datePanel.add(fetchButton);
        topPanel.add(datePanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Table to show attendance details
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Subject");
        tableModel.addColumn("Attendance");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Adjust column widths
        TableColumn column = null;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) { // Subject column
                column.setPreferredWidth(150); // Adjust the width as per your requirement
            } else if (i == 1) { // Attendance column
                column.setPreferredWidth(100); // Adjust the width as per your requirement
            }
        }

        // JTextArea for notifications
        notificationsArea = new JTextArea(5, 20);
        notificationsArea.setEditable(false); // To make it read-only
        JScrollPane notificationsScrollPane = new JScrollPane(notificationsArea);
        add(notificationsScrollPane, BorderLayout.SOUTH);

        attendanceService = new AttendanceServiceProxy(); // Use the proxy service
    }

    private void fetchAttendance() {
        String date = dateField.getText().trim();
        if (!date.isEmpty()) {
            attendanceService.loadAttendanceDetails(tableModel, date, email);
            // Fetch notifications from the database based on email
            String notifications = fetchNotifications(email);
            notificationsArea.setText(notifications);
        } else {
            JOptionPane.showMessageDialog(this, "Please enter a date.");
        }
    }

    // Method to fetch notifications from the database based on email
    private String fetchNotifications(String email) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        StringBuilder notifications = new StringBuilder();

        try {
            // Establish database connection
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance_management_system", "root", "");

            // Prepare SQL query
            String sql = "SELECT notification FROM students WHERE email = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            // Execute the query
            resultSet = statement.executeQuery();

            // Process the result
            while (resultSet.next()) {
                notifications.append(resultSet.getString("notification")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return notifications.toString();
    }
}
