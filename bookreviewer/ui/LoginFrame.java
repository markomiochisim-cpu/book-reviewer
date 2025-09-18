package ui;

import storage.UserStorage;
import theme.ThemeManager;

import javax.swing.*;

public class LoginFrame extends JFrame {
	public interface OnLoggedIn {
		void onLoggedIn(String userId);
	}

	public LoginFrame(UserStorage storage, ThemeManager theme, OnLoggedIn callback) {
		super("Login");
		setSize(420, 220);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel idLabel = new JLabel("User ID:");
		idLabel.setBounds(50, 50, 100, 30);
		add(idLabel);

		JTextField idField = new JTextField();
		idField.setBounds(150, 50, 180, 30);
		add(idField);

		JButton loginBtn = new JButton("Login");
		loginBtn.setBounds(150, 100, 100, 30);
		add(loginBtn);

		loginBtn.addActionListener(e -> {
			String userId = idField.getText().trim();
			if (userId.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Please enter your user ID", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (!storage.userExists(userId)) {
				JOptionPane.showMessageDialog(this, "Invalid user ID", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			dispose();
			callback.onLoggedIn(userId);
		});

		theme.applyTo(getContentPane());
		setVisible(true);
	}
}


