package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class HelpPages {
	public static void showAbout(JFrame owner) {
		JTextArea area = new JTextArea("Book Reviewer helps you browse 10 classic books, track status, and save favorites.");
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		JOptionPane.showMessageDialog(owner, new JScrollPane(area), "About", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showSupport(JFrame owner) {
		JTextArea area = new JTextArea("Support: For issues, use Help > Report to send feedback.");
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setEditable(false);
		JOptionPane.showMessageDialog(owner, new JScrollPane(area), "Support", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showReport(JFrame owner) {
		JDialog dialog = new JDialog(owner, "Report", true);
		dialog.setSize(500, 400);
		dialog.setLocationRelativeTo(owner);
		dialog.setLayout(new BorderLayout(8, 8));

		JCheckBox abuse = new JCheckBox("Report abuse");
		dialog.add(abuse, BorderLayout.NORTH);

		JTextArea area = new JTextArea();
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		dialog.add(new JScrollPane(area), BorderLayout.CENTER);

		JButton submit = new JButton("Submit Report");
		dialog.add(submit, BorderLayout.SOUTH);

		submit.addActionListener(e -> {
			try {
				File reportsDir = new File("reports");
				if (!reportsDir.exists()) reportsDir.mkdirs();
				File file = File.createTempFile("report_", ".txt", reportsDir);
				try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
					bw.write("Abuse:" + abuse.isSelected() + "\n");
					bw.write(area.getText());
				}
				JOptionPane.showMessageDialog(dialog, "Report submitted. Thank you!");
				dialog.dispose();
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(dialog, "Failed to save report.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		});

		dialog.setVisible(true);
	}
}


