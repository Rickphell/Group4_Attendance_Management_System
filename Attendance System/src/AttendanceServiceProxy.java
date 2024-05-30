import javax.swing.table.DefaultTableModel;
import attendanceManagement.AttendanceService;

class AttendanceServiceProxy implements AttendanceService {
    private AttendanceServiceImpl realService;

    public AttendanceServiceProxy() {
        this.realService = new AttendanceServiceImpl();
    }

    @Override
    public void loadAttendanceDetails(DefaultTableModel tableModel, String date, String email) {
        System.out.println("Fetching attendance details for " + email + " on " + date);
        realService.loadAttendanceDetails(tableModel, date, email);
    }
}
