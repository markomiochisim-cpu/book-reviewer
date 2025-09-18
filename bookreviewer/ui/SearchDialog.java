package ui;

import model.Book;
import storage.UserStorage;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class SearchDialog extends JDialog {
	private final JList<Book> resultsList = new JList<>();
	private final JLabel imageLabel = new JLabel();
	private final JLabel titleLabel = new JLabel();
	private final JLabel authorLabel = new JLabel();
	private final JLabel ratingLabel = new JLabel();
	private final JTextArea descriptionArea = new JTextArea();

	public interface Listener {
		void onFavorite(Book book);
		void onStatus(Book book, String status);
		void onRate(Book book, double newRating);
	}

	public SearchDialog(JFrame owner, List<Book> matches, Set<String> favorites, Map<String, String> statuses, Map<String, Double> userRatings, Listener listener) {
		super(owner, "Search Results", true);
		setSize(800, 520);
		setLocationRelativeTo(owner);
		setLayout(new BorderLayout(8, 8));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		DefaultListModel<Book> listModel = new DefaultListModel<>();
		for (Book b : matches) listModel.addElement(b);
		resultsList.setModel(listModel);
		resultsList.setCellRenderer(new DefaultListCellRenderer() {
			@Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component c = super.getListCellRendererComponent(list, ((Book)value).getName(), index, isSelected, cellHasFocus);
				return c;
			}
		});
		add(new JScrollPane(resultsList), BorderLayout.WEST);
		((JScrollPane)getContentPane().getComponent(0)).setPreferredSize(new Dimension(220, 0));

		JPanel preview = new JPanel(new BorderLayout(8, 8));
		JPanel top = new JPanel(new BorderLayout(8, 8));
		imageLabel.setPreferredSize(new Dimension(160, 240));
		top.add(imageLabel, BorderLayout.WEST);
		JPanel meta = new JPanel();
		meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
		meta.add(titleLabel);
		meta.add(authorLabel);
		meta.add(ratingLabel);
		top.add(meta, BorderLayout.CENTER);
		preview.add(top, BorderLayout.NORTH);

		descriptionArea.setLineWrap(true);
		descriptionArea.setWrapStyleWord(true);
		descriptionArea.setEditable(false);
		preview.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

		JPanel actions = new JPanel();
		JButton favBtn = new JButton("❤");
		JComboBox<String> status = new JComboBox<>(new String[]{"Read Later", "Reading", "Completed"});
		JButton rateBtn = new JButton("Rate");
		actions.add(status);
		actions.add(favBtn);
		actions.add(rateBtn);
		preview.add(actions, BorderLayout.SOUTH);

		add(preview, BorderLayout.CENTER);

		resultsList.addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) showBook(resultsList.getSelectedValue(), favorites, statuses, userRatings); });

		favBtn.addActionListener(e -> { Book b = resultsList.getSelectedValue(); if (b != null) listener.onFavorite(b); });
		status.addActionListener(e -> { Book b = resultsList.getSelectedValue(); if (b != null) listener.onStatus(b, (String) status.getSelectedItem()); });
		rateBtn.addActionListener(e -> { Book b = resultsList.getSelectedValue(); if (b != null) {
			String s = JOptionPane.showInputDialog(this, "Rate 1-10");
			if (s == null) return;
			try {
				double user = Math.max(1, Math.min(10, Double.parseDouble(s.trim())));
				double base = userRatings.getOrDefault(b.getId(), b.getRating());
				double newRating = (base + user) / 2.0; // store exactly what we show
				listener.onRate(b, newRating);
			} catch (NumberFormatException ignored) {}
		}});

		getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);

		if (!matches.isEmpty()) { resultsList.setSelectedIndex(0); }
	}

	private void showBook(Book b, Set<String> favorites, Map<String, String> statuses, Map<String, Double> userRatings) {
		if (b == null) return;
		imageLabel.setIcon(b.loadImageIcon(220));
		titleLabel.setText(b.getName());
		authorLabel.setText(b.getAuthor());
		double r = b.getRating();
		if (userRatings.containsKey(b.getId())) r = (r + userRatings.get(b.getId())) / 2.0;
		ratingLabel.setText("Rating: " + String.format(Locale.US, "%.1f", r));
		descriptionArea.setText(b.getDescription());
	}
}


