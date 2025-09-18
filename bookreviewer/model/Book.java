package model;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Book {
	private final String id;
	private final String name;
	private final String author;
	private final double rating;
	private final String description;
	private final String imagePath;

	public Book(String id, String name, String author, double rating, String description, String imagePath) {
		this.id = id;
		this.name = name;
		this.author = author;
		this.rating = rating;
		this.description = description;
		this.imagePath = imagePath;
	}

	public String getId() { return id; }
	public String getName() { return name; }
	public String getAuthor() { return author; }
	public double getRating() { return rating; }
	public String getDescription() { return description; }
	public String getImagePath() { return imagePath; }

	public ImageIcon loadImageIcon(int targetHeight) {
		if (imagePath == null) return null;
		File file = new File(imagePath);
		if (!file.exists()) return null;
		ImageIcon original = new ImageIcon(imagePath);
		if (original.getIconHeight() <= 0) return null;
		double scale = (double) targetHeight / original.getIconHeight();
		int width = (int) Math.max(1, Math.round(original.getIconWidth() * scale));
		Image scaled = original.getImage().getScaledInstance(width, targetHeight, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
}


