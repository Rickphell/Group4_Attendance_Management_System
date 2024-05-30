package attendanceManagement;

public interface AttendanceHandler {
    void setNextHandler(AttendanceHandler nextHandler);
    void handle(AttendanceRequest request);
}
