import javax.swing.*;
import attendanceManagement.LoginHandler;

public class EmptyFieldHandler extends LoginHandler {

    public EmptyFieldHandler(LoginHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean handle(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return false;
        }
        if (nextHandler != null) {
            return nextHandler.handle(username, password);
        }
        return true;
    }
}
