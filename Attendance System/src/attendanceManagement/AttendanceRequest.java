package attendanceManagement;
public class AttendanceRequest {
    private String subject;
    private int studentId;
    private boolean status;

    public AttendanceRequest(String subject, int studentId, boolean status) {
        this.subject = subject;
        this.studentId = studentId;
        this.status = status;
    }

    public String getSubject() {
        return subject;
    }

    public int getStudentId() {
        return studentId;
    }

    public boolean isStatus() {
        return status;
    }
}
