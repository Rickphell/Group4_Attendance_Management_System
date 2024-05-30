import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import attendanceManagement.LoginHandler;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;

    public LoginForm() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        placeComponents(panel);
        add(panel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());

                // Create the chain of handlers
                LoginHandler handlerChain = new EmptyFieldHandler(
                        new UsernamePasswordHandler(null)
                );

                // Start the chain
                if (handlerChain.handle(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    dispose(); // Close the login form

                    // Fetch user details from the database
                    User user = getUserDetailsFromDatabase(username);

                    if (user != null) {
                        // Redirect based on role
                        if ("lecturer".equalsIgnoreCase(user.getRole())) {
                            new StudentDetailsPage(user.getSubject()).setVisible(true); // Open the student details page with selected subject
                        } else {
                            new StudentPage(user.getEmail()).setVisible(true); // Open the student page with selected subject and email
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.");
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpForm().setVisible(true);
                dispose(); // Close the login form
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 20, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 20, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 50, 165, 25);
        panel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(10, 80, 80, 25);
        panel.add(loginButton);

        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(180, 80, 80, 25);
        panel.add(signUpButton);
    }

    private User getUserDetailsFromDatabase(String username) {
        User user = null;
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT subject, role, email FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String subject = resultSet.getString("subject");
                String role = resultSet.getString("role");
                String email = resultSet.getString("email");
                user = new User(username, subject, role, email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}

class User {
    private String username;
    private String subject;
    private String role;
    private String email;

    public User(String username, String subject, String role, String email) {
        this.username = username;
        this.subject = subject;
        this.role = role;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getSubject() {
        return subject;
    }
    
    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }
}
