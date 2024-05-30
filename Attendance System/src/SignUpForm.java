import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<String> subjectDropdown;
    private JButton signUpButton;
    private JButton backButton;

    public SignUpForm() {
        setTitle("Sign Up");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        placeComponents(panel);
        add(panel);

        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String subject = (String) subjectDropdown.getSelectedItem();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields.");
                    return;
                }

                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
                if (!email.matches(emailRegex) || !email.endsWith(".gcit@rub.edu.bt") || email.split("@")[0].matches("^[0-9]+$")) {
                    JOptionPane.showMessageDialog(null, "Invalid email format. Must be xxxxxx.gcit@rub.edu.bt");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match!");
                    return;
                }

                if (isUsernameTaken(username)) {
                    JOptionPane.showMessageDialog(null, "Username already taken!");
                    return;
                }

                boolean signUpSuccess = signUp(name, email, subject, username, password);
                if (signUpSuccess) {
                    JOptionPane.showMessageDialog(null, "Sign Up successful!");
                    dispose(); // Close the sign-up form
                    new LoginForm().setVisible(true); // Open the login form
                } else {
                    JOptionPane.showMessageDialog(null, "Sign Up failed!");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the sign-up form
                new LoginForm().setVisible(true); // Open the login form
            }
        });
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 20, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(100, 20, 165, 25);
        panel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 50, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(100, 50, 165, 25);
        panel.add(emailField);

        JLabel subjectLabel = new JLabel("Subject:");
        subjectLabel.setBounds(10, 80, 80, 25);
        panel.add(subjectLabel);

        String[] subjects = { "DSP", "CSF", "PRJ", "ENT" };
        subjectDropdown = new JComboBox<>(subjects);
        subjectDropdown.setBounds(100, 80, 165, 25);
        panel.add(subjectDropdown);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 110, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 110, 165, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 140, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 140, 165, 25);
        panel.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(10, 170, 140, 25);
        panel.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setBounds(150, 170, 165, 25);
        panel.add(confirmPasswordField);

        signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(10, 200, 150, 25);
        panel.add(signUpButton);

        backButton = new JButton("Back to Login");
        backButton.setBounds(200, 200, 150, 25);
        panel.add(backButton);
    }

    private boolean isUsernameTaken(String username) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean signUp(String name, String email, String subject, String username, String password) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            String query = "INSERT INTO users (name, email, subject, username, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, subject);
            preparedStatement.setString(4, username);
            preparedStatement.setString(5, password);
            int result = preparedStatement.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new SignUpForm().setVisible(true);
    }
}
