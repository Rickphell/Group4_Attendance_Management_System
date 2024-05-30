import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class ShowDetailsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;

    public ShowDetailsPanel() {
        setLayout(new BorderLayout());

        JLabel label = new JLabel("Student Details");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.NORTH);

        // Table to show student details
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Enrollment");
        tableModel.addColumn("Name");
        tableModel.addColumn("Email");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Adjust column widths
        TableColumn column = null;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) { // ID column
                column.setPreferredWidth(50); // Adjust the width as per your requirement
            } else if (i == 1) { // Name column
                column.setPreferredWidth(150); // Adjust the width as per your requirement
            } else if (i == 2) { // Email column
                column.setPreferredWidth(200); // Adjust the width as per your requirement
            }
        }

        // Load student details
        loadStudentDetails();
    }

    private void loadStudentDetails() {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT enrollment, name, email FROM students";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("enrollment");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                tableModel.addRow(new Object[]{id, name, email});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
