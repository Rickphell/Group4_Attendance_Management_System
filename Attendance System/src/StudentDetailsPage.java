import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class StudentDetailsPage extends JFrame {
    public StudentDetailsPage(String subject) {
        setTitle("Student Details");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create panels for each tab
        JPanel showDetailsPanel = new ShowDetailsPanel();
        JPanel takeAttendancePanel = new TakeAttendancePanel(subject);
        JPanel editAttendancePanel = new EditAttendancePanel(subject);
        JPanel reportPanel = new ReportPanel();

        // Add panels to the tabbed pane
        tabbedPane.addTab("Show Student Details", showDetailsPanel);
        tabbedPane.addTab("Take Attendance", takeAttendancePanel);
        tabbedPane.addTab("Edit Attendance", editAttendancePanel);
        tabbedPane.addTab("Report", reportPanel);

        add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDetailsPage("Design Pattern"));
    }

    public double getAttendancePercentage() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttendancePercentage'");
    }
}
