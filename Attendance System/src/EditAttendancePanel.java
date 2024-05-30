import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditAttendancePanel extends JPanel {
    protected DefaultTableModel tableModel;
    protected Student[] students;
    protected String subject; // Subject selected during registration
    protected JTextField dateField;
    private JTable table;
    private JButton goButton;
    private JButton saveButton;
    private Invoker invoker;

    public EditAttendancePanel(String subject) {
        this.subject = subject;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());

        JLabel label = new JLabel("Edit Attendance for " + subject);
        topPanel.add(label);

        dateField = new JTextField(10);
        LocalDate currentDate = LocalDate.now();
        dateField.setText(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        topPanel.add(dateField);

        goButton = new JButton("Go");
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadAttendanceData();
            }
        });
        topPanel.add(goButton);

        add(topPanel, BorderLayout.NORTH);

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

        saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAttendance();
            }
        });
        add(saveButton, BorderLayout.SOUTH);

        // Initialize the Invoker
        invoker = new Invoker();
    }

    private void loadAttendanceData() {
        invoker.setLoadCommand(new LoadAttendanceCommand(this));
        invoker.loadAttendance();
    }

    private void saveAttendance() {
        invoker.setSaveCommand(new SaveAttendanceCommand(this, students));
        invoker.saveAttendance();
    }

    protected String getCurrentDateField() {
        return dateField.getText().replace("-", "_");
    }
}
