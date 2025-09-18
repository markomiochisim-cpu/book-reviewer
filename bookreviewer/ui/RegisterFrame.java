package ui;

import storage.UserStorage;
import theme.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
	public interface OnRegistered {
		void onRegistered(String userId);
	}

	public RegisterFrame(UserStorage storage, ThemeManager theme, OnRegistered callback) {
        super("Register User");
        setSize(420, 420);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        add(userLabel);

        JTextField userField = new JTextField();
        userField.setBounds(160, 50, 180, 30);
        add(userField);

        JLabel rollLabel = new JLabel("Roll Number:");
        rollLabel.setBounds(50, 100, 100, 30);
        add(rollLabel);

        JTextField rollField = new JTextField();
        rollField.setBounds(160, 100, 180, 30);
        add(rollField);

        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setBounds(50, 150, 100, 30);
        add(ageLabel);

        JTextField ageField = new JTextField();
        ageField.setBounds(160, 150, 180, 30);
        add(ageField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 200, 100, 30);
        add(emailLabel);

        JTextField emailField = new JTextField();
        emailField.setBounds(160, 200, 180, 30);
        add(emailField);

        JButton registerBtn = new JButton("Sign In");
        registerBtn.setBounds(160, 260, 120, 32);
        add(registerBtn);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String roll = rollField.getText().trim();
            String age = ageField.getText().trim();
            String email = emailField.getText().trim();
            if (username.isEmpty() || roll.isEmpty() || age.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                String userId = storage.register(username, roll, age, email);
                JOptionPane.showMessageDialog(this, "Sign in completed!");
                dispose();
                callback.onRegistered(userId);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Age must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving data", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

		theme.applyTo(getContentPane());
		setVisible(true);
	}
}


