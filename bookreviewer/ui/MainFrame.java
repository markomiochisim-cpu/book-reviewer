package ui;

import model.Book;
import storage.UserStorage;
import theme.ThemeManager;
import util.CSVLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import util.Config;
import java.util.List;
import java.util.*;

public class MainFrame extends JFrame implements BookPanel.Listener {
	private final String userId;
	private final UserStorage storage;
	private final ThemeManager theme;
	private final Config config = new Config(new File("config.properties"));
	private final List<Book> allBooks;
	private final Set<String> favoriteIds = new HashSet<>();
	private final Map<String, String> statuses = new HashMap<>();
	private final Map<String, Double> ratings = new HashMap<>();
	private final DefaultListModel<Book> listModel = new DefaultListModel<>();

	public MainFrame(String userId, UserStorage storage, ThemeManager theme) {
		super("Book Reviewer - Welcome " + userId);
		this.userId = userId;
		this.storage = storage;
		this.theme = theme;

		File csv = new File("info.csv");
		File pics = new File("pictures");
		this.allBooks = CSVLoader.loadBooks(csv, pics);

		storage.loadUserState(userId, favoriteIds, statuses, ratings);

		setSize(1000, 680);
		setLayout(new BorderLayout(8, 8));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel topBar = buildTopBar();
		add(topBar, BorderLayout.NORTH);

		JPanel content = new JPanel(new BorderLayout(8, 8));
		JPanel booksPanel = new JPanel();
		booksPanel.setLayout(new BoxLayout(booksPanel, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane(booksPanel);
		content.add(scroll, BorderLayout.CENTER);
		add(content, BorderLayout.CENTER);

		updateBookList(booksPanel, allBooks);

		theme.applyTo(getContentPane());
		setVisible(true);
	}

	private JPanel buildTopBar() {
		JPanel panel = new JPanel(null);
		panel.setPreferredSize(new Dimension(1000, 90));

		// removed window-close button next to Theme per request

		JButton favoritesButton = new JButton("Favorites");
		favoritesButton.setBounds(850, 50, 120, 30);
		favoritesButton.addActionListener(e -> showFavorites());
		panel.add(favoritesButton);

		JTextField searchField = new JTextField();
		searchField.setBounds(20, 50, 220, 30);
		panel.add(searchField);

		JButton searchButton = new JButton("Search");
		searchButton.setBounds(250, 50, 90, 30);
		panel.add(searchButton);

		// removed view filter combo per request

		boolean dark = "theme_dark".equals(config.get("theme", "theme_dark"));
		JToggleButton themeToggle = new JToggleButton(dark ? "Theme: Dark" : "Theme: Light");
		themeToggle.setBounds(770, 10, 150, 30);
		themeToggle.setSelected(dark);
		panel.add(themeToggle);

		JComboBox<String> help = new JComboBox<>(new String[]{"Help", "About", "Support", "Report"});
		help.setBounds(440, 10, 140, 30);
		help.addActionListener(e -> {
			String s = (String) help.getSelectedItem();
			if ("About".equals(s)) { HelpPages.showAbout(this); help.setSelectedIndex(0);} 
			else if ("Support".equals(s)) { HelpPages.showSupport(this); help.setSelectedIndex(0);} 
			else if ("Report".equals(s)) { HelpPages.showReport(this); help.setSelectedIndex(0);} 
		});
		panel.add(help);

		searchButton.addActionListener(e -> openSearch(searchField.getText()));
		searchField.addActionListener(e -> openSearch(searchField.getText()));
		themeToggle.addActionListener(e -> { toggleTheme(themeToggle.isSelected()); themeToggle.setText(themeToggle.isSelected()?"Theme: Dark":"Theme: Light"); });

		return panel;
	}

	private void openSearch(String query) {
		String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
		List<Book> matches = new ArrayList<>();
		for (Book b : allBooks) {
			if (q.isEmpty() || b.getName().toLowerCase(Locale.ROOT).contains(q)) matches.add(b);
		}
		SearchDialog dialog = new SearchDialog(this, matches, favoriteIds, statuses, ratings, new SearchDialog.Listener() {
			@Override public void onFavorite(Book book) { toggleFavorite(book); }
			@Override public void onStatus(Book book, String status) { onStatusChanged(book, status); refreshList(); }
			@Override public void onRate(Book book, double newRating) { ratings.put(book.getId(), newRating); storage.saveUserState(userId, favoriteIds, statuses, ratings); refreshList(); SwingUtilities.invokeLater(() -> {
				// also update any open BookPanel instances immediately
				JScrollPane scroll = (JScrollPane)((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
				JPanel booksPanel = (JPanel) scroll.getViewport().getView();
				for (Component c : booksPanel.getComponents()) {
					if (c instanceof BookPanel) {
						BookPanel bp = (BookPanel) c;
						// no direct link to book instance, refreshList already rebuilt with new ratings
					}
				}
			}); }
		});
		dialog.setVisible(true);
	}

	private void refreshList() {
		JScrollPane scroll = (JScrollPane)((BorderLayout)getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
		JPanel booksPanel = (JPanel) scroll.getViewport().getView();
		updateBookList(booksPanel, allBooks);
	}

	private void updateBookList(JPanel container, List<Book> books) {
		container.removeAll();
		for (Book b : books) {
			BookPanel bp = new BookPanel(b, this);
			bp.setFavorite(favoriteIds.contains(b.getId()));
			String st = statuses.get(b.getId());
			if (st != null) bp.setStatus(st);
			double r = b.getRating();
			if (ratings.containsKey(b.getId())) r = ratings.get(b.getId());
			bp.setRating(r);
			container.add(bp);
			container.add(Box.createVerticalStrut(8));
		}
		container.revalidate();
		container.repaint();
	}

	private void showFavorites() {
		List<Book> favs = new ArrayList<>();
		for (Book b : allBooks) if (favoriteIds.contains(b.getId())) favs.add(b);
		FavoritesDialog dialog = new FavoritesDialog(this, favs);
		dialog.setVisible(true);
	}

	@Override
	public void onFavorite(Book book) {
		toggleFavorite(book);
	}

	@Override
	public void onStatusChanged(Book book, String status) {
		if (status == null) return;
		statuses.put(book.getId(), status);
		storage.saveUserState(userId, favoriteIds, statuses, ratings);
	}

	private void toggleTheme(boolean dark) {
		File f = new File(dark ? "theme_dark.css" : "theme_light.css");
		theme.setThemeFile(f);
		theme.applyTo(getContentPane());
		repaint();
		config.set("theme", dark ? "theme_dark" : "theme_light");
	}

	private void toggleFavorite(Book book) {
		if (favoriteIds.contains(book.getId())) favoriteIds.remove(book.getId()); else favoriteIds.add(book.getId());
		if (favoriteIds.contains(book.getId())) JOptionPane.showMessageDialog(this, "This has been added to the favorites");
		storage.saveUserState(userId, favoriteIds, statuses, ratings);
		refreshList();
	}
}


