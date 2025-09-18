package ui;

import model.Book;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FavoritesDialog extends JDialog {
	public FavoritesDialog(JFrame owner, List<Book> favorites) {
		super(owner, "Favorites", true);
		setSize(500, 400);
		setLocationRelativeTo(owner);

		DefaultListModel<String> model = new DefaultListModel<>();
		for (Book b : favorites) model.addElement(b.getName());
		JList<String> list = new JList<>(model);
		add(new JScrollPane(list), BorderLayout.CENTER);
	}
}


