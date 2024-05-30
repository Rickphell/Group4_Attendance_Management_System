import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import attendanceManagement.AttendanceReportGenerator;
import attendanceManagement.NotificationMediator;

public class ReportPanel extends JPanel {
    private JTable table;
    private JComboBox<String> reportTypeComboBox;
    private JButton generateButton;
    // private JButton notifyButton;
    private AttendanceReportFactory reportFactory;
    private LocalDate startDate;
    private LocalDate endDate;  
    private NotificationMediator mediator;

    public ReportPanel() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Select Report Type:");
        topPanel.add(label);

        reportTypeComboBox = new JComboBox<>(new String[]{"Weekly", "Monthly"});
        topPanel.add(reportTypeComboBox);

        generateButton = new JButton("Generate Report");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        topPanel.add(generateButton);

        add(topPanel, BorderLayout.NORTH);

        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // notifyButton = new JButton("Notify");
        // notifyButton.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         notifyAction();
        //     }
        // });
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        // bottomPanel.add(notifyButton);
        add(bottomPanel, BorderLayout.SOUTH);

        reportFactory = new AttendanceReportFactory();

        mediator = new DatabaseConnectionMediator(); 
    }

    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        AttendanceReportGenerator generator = reportFactory.createGenerator(reportType, startDate, endDate);
        String[][] reportData = generator.generateReport();
        if (reportData != null) {
            String[] columnNames = {"Name", "Average (%)"};
            DefaultTableModel model = new DefaultTableModel(reportData, columnNames);
            table.setModel(model);

            // Center alignment for "Average (%)" column
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
            }
        }

    private class WeeklyAttendanceReportGenerator implements AttendanceReportGenerator {
        private LocalDate startDate;
        private LocalDate endDate;

        public WeeklyAttendanceReportGenerator(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public String[][] generateReport() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            startDate = LocalDate.of(2024, 5, 1);
            endDate = LocalDate.of(2024, 5, 7);

            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("SELECT name, ");

                // Calculate the sum of attendance for each day of the week
                LocalDate currentDate = startDate;
                List<String> dateColumns = new ArrayList<>();
                while (!currentDate.isAfter(endDate)) {
                    String currentFormattedDate = currentDate.format(formatter);
                    dateColumns.add(currentFormattedDate);
                    queryBuilder.append("COALESCE(").append(currentFormattedDate).append(", 0) + ");
                    currentDate = currentDate.plusDays(1);
                }
                queryBuilder.setLength(queryBuilder.length() - 3); // Remove the last " + "
                queryBuilder.append(" AS weekly_total, ");

                // Calculate the average attendance for the week
                queryBuilder.append("(");
                for (String date : dateColumns) {
                    queryBuilder.append("COALESCE(").append(date).append(", 0) + ");
                }
                queryBuilder.setLength(queryBuilder.length() - 3); // Remove the last " + "
                queryBuilder.append(") / ").append(dateColumns.size()).append(" AS weekly_average ");
                queryBuilder.append("FROM dsp");

                String query = queryBuilder.toString();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String[]> data = new ArrayList<>();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    double weeklyAverage = resultSet.getDouble("weekly_average") * 100;
                    String remarks = null;
                    if (weeklyAverage < 90) {
                        remarks = "Your attendance avg is below 90";
                    }
                    updateStudentRemarks(name, remarks);
                    data.add(new String[]{name, String.format("%.2f", weeklyAverage)});
                    mediator.updateNotification(name, remarks);
                }

                return data.toArray(new String[0][]);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void updateStudentRemarks(String name, String remarks) {
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "UPDATE dsp SET remarks = ? WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                if (remarks != null) {
                    preparedStatement.setString(1, remarks);
                } else {
                    preparedStatement.setNull(1, java.sql.Types.VARCHAR);
                }
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class MonthlyAttendanceReportGenerator implements AttendanceReportGenerator {
        private LocalDate startDate;
        private LocalDate endDate;

        public MonthlyAttendanceReportGenerator(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public String[][] generateReport() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            startDate = LocalDate.of(2024, 5, 1);
            endDate = LocalDate.of(2024, 5, 31);

            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                StringBuilder queryBuilder = new StringBuilder();
                queryBuilder.append("SELECT name, ");

                // Calculate the sum of attendance for each day of the month
                LocalDate currentDate = startDate;
                List<String> dateColumns = new ArrayList<>();
                while (!currentDate.isAfter(endDate)) {
                    String currentFormattedDate = currentDate.format(formatter);
                    dateColumns.add(currentFormattedDate);
                    queryBuilder.append("COALESCE(").append(currentFormattedDate).append(", 0) + ");
                    currentDate = currentDate.plusDays(1);
                }
                queryBuilder.setLength(queryBuilder.length() - 3); // Remove the last " + "
                queryBuilder.append(" AS monthly_total, ");

                // Calculate the average attendance for the month
                queryBuilder.append("(");
                for (String date : dateColumns) {
                    queryBuilder.append("COALESCE(").append(date).append(", 0) + ");
                }
                queryBuilder.setLength(queryBuilder.length() - 3); // Remove the last " + "
                queryBuilder.append(") / ").append(dateColumns.size()).append(" AS monthly_average ");
                queryBuilder.append("FROM dsp");

                String query = queryBuilder.toString();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();

                List<String[]> data = new ArrayList<>();

                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    double monthlyAverage = resultSet.getDouble("monthly_average") * 100;
                    String remarks = null;
                    if (monthlyAverage < 90) {
                        remarks = "Your attendance avg is below 90";
                    }
                    updateStudentRemarks(name, remarks);
                    data.add(new String[]{name, String.format("%.2f", monthlyAverage)});

                    // Update notification value using the mediator
                    mediator.updateNotification(name, remarks);
                }

                return data.toArray(new String[0][]);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private void updateStudentRemarks(String name, String remarks) {
            try {
                Connection connection = DatabaseConnection.getInstance().getConnection();
                String query = "UPDATE dsp SET remarks = ? WHERE name = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                if (remarks != null) {
                    preparedStatement.setString(1, remarks);
                } else {
                    preparedStatement.setNull(1, java.sql.Types.VARCHAR);
                }
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class AttendanceReportFactory {
        public AttendanceReportGenerator createGenerator(String reportType, LocalDate startDate, LocalDate endDate) {
            switch (reportType) {
                case "Weekly":
                    return new WeeklyAttendanceReportGenerator(startDate, endDate);
                case "Monthly":
                    return new MonthlyAttendanceReportGenerator(startDate, endDate);
                default:
                    throw new IllegalArgumentException("Invalid report type: " + reportType);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Attendance Report");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ReportPanel());
        frame.setVisible(true);
    }
}
