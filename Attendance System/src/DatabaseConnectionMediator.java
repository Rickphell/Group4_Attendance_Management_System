import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import attendanceManagement.NotificationMediator;

public class DatabaseConnectionMediator implements NotificationMediator {
    @Override
    public void updateNotification(String name, String notification) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "UPDATE students SET notification = ? WHERE name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, notification);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}