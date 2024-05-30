package attendanceManagement;

public abstract class LoginHandler {
    protected LoginHandler nextHandler;

    public LoginHandler(LoginHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract boolean handle(String username, String password);
}
