package ui;

import model.Book;

import javax.swing.*;
import java.awt.*;


public class BookPanel extends JPanel {
	private final JToggleButton favoriteButton;
	private final JComboBox<String> statusCombo;
    private final JLabel ratingLabel;

	public interface Listener {
		void onFavorite(Book book);
		void onStatusChanged(Book book, String status);
	}

	public BookPanel(Book book, Listener listener) {
		setLayout(new BorderLayout(8, 8));
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		JLabel imageLabel = new JLabel();
		imageLabel.setPreferredSize(new Dimension(140, 200));
		ImageIcon icon = book.loadImageIcon(180);
		if (icon != null) imageLabel.setIcon(icon);
		add(imageLabel, BorderLayout.WEST);

		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		JLabel name = new JLabel(book.getName());
		name.setFont(name.getFont().deriveFont(Font.BOLD, 16f));
		center.add(name);
		JLabel author = new JLabel(book.getAuthor());
		center.add(author);
		ratingLabel = new JLabel("Rating: " + book.getRating());
		center.add(ratingLabel);
		JTextArea description = new JTextArea(book.getDescription());
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		description.setOpaque(false);
		center.add(description);
		add(center, BorderLayout.CENTER);

		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		favoriteButton = new JToggleButton("♡");
		favoriteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		right.add(favoriteButton);
		right.add(Box.createVerticalStrut(12));
		statusCombo = new JComboBox<>(new String[]{"", "Completed", "Reading", "Read Later"});
		statusCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
		right.add(statusCombo);
		add(right, BorderLayout.EAST);

		addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override public void componentResized(java.awt.event.ComponentEvent e) {
				int w = getWidth();
				if (w < 600) {
					removeAll();
					JPanel row = new JPanel();
					row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
					JLabel smallImg = new JLabel(imageLabel.getIcon());
					smallImg.setPreferredSize(new Dimension(80, 120));
					row.add(smallImg);
					JPanel text = new JPanel();
					text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
					text.add(new JLabel(book.getName()));
					JLabel shortDesc = new JLabel(truncate(description.getText(), 120));
					text.add(shortDesc);
					row.add(Box.createHorizontalStrut(8));
					row.add(text);
					row.add(Box.createHorizontalGlue());
					row.add(right);
					setLayout(new BorderLayout());
					add(row, BorderLayout.CENTER);
					revalidate(); repaint();
				} else {
					removeAll();
					setLayout(new BorderLayout(8,8));
					add(imageLabel, BorderLayout.WEST);
					add(center, BorderLayout.CENTER);
					add(right, BorderLayout.EAST);
					revalidate(); repaint();
				}
			}
		});

		favoriteButton.addActionListener(e -> listener.onFavorite(book));
		statusCombo.addActionListener(e -> listener.onStatusChanged(book, (String) statusCombo.getSelectedItem()));
	}

	private static String truncate(String s, int n) { return s.length() <= n ? s : s.substring(0, n - 3) + "..."; }

	public void setRating(double rating) { ratingLabel.setText("Rating: " + String.format(java.util.Locale.US, "%.1f", rating)); }

	public void setFavorite(boolean fav) {
		favoriteButton.setSelected(fav);
		favoriteButton.setText(fav ? "❤" : "♡");
	}

	public void setStatus(String status) {
		if (status == null) return;
		statusCombo.setSelectedItem(status);
	}
}


