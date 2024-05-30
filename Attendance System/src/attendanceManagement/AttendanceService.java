package attendanceManagement;
import javax.swing.table.DefaultTableModel;

public interface AttendanceService {
    void loadAttendanceDetails(DefaultTableModel tableModel, String date, String email);
}