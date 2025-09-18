package util;

import model.Book;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVLoader {
	public static List<Book> loadBooks(File csvFile, File picturesDir) {
		List<Book> books = new ArrayList<>();
		if (csvFile == null || !csvFile.exists()) return books;
		Map<String, String> imageMap = buildImageMap(picturesDir);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) continue;
				// Expecting: Id,Name,Author,Rating,Description
				String[] parts = line.split(",");
				if (parts.length < 5) continue;
				String id = parts[0].trim();
				String name = parts[1].trim();
				String author = parts[2].trim();
				double rating = 0.0;
				try { rating = Double.parseDouble(parts[3].trim()); } catch (NumberFormatException ignored) {}
				StringBuilder descBuilder = new StringBuilder();
				for (int i = 4; i < parts.length; i++) {
					if (i > 3) descBuilder.append(",");
					descBuilder.append(parts[i]);
				}
				String description = descBuilder.toString().trim();
				String imagePath = imageMap.getOrDefault(name, null);
				books.add(new Book(id, name, author, rating, description, imagePath));
			}
		} catch (IOException ignored) {}
		return books;
	}

	private static Map<String, String> buildImageMap(File picturesDir) {
		Map<String, String> map = new HashMap<>();
		if (picturesDir == null || !picturesDir.exists() || !picturesDir.isDirectory()) return map;
		File[] files = picturesDir.listFiles();
		if (files == null) return map;
		for (File f : files) {
			String name = f.getName();
			int dot = name.lastIndexOf('.');
			if (dot > 0) name = name.substring(0, dot);
			name = name.replace('_', ' ');
			map.put(name, f.getAbsolutePath());
		}
		return map;
	}
}


