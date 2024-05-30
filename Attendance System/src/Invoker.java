import attendanceManagement.Command;
public class Invoker {
    private Command loadCommand;
    private Command saveCommand;

    public void setLoadCommand(Command loadCommand) {
        this.loadCommand = loadCommand;
    }

    public void setSaveCommand(Command saveCommand) {
        this.saveCommand = saveCommand;
    }

    public void loadAttendance() {
        loadCommand.execute();
    }

    public void saveAttendance() {
        saveCommand.execute();
    }
}
