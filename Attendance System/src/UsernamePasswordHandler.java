import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import attendanceManagement.LoginHandler;

public class UsernamePasswordHandler extends LoginHandler {

    public UsernamePasswordHandler(LoginHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean handle(String username, String password) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true; // Successful login
            } else if (nextHandler != null) {
                return nextHandler.handle(username, password);
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
