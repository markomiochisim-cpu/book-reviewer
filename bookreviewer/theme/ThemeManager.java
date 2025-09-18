package theme;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ThemeManager {
	private final Properties props = new Properties();

	public ThemeManager(File cssLikeFile) {
		load(cssLikeFile);
	}

	private File currentFile;

	private void load(File file) {
		currentFile = file;
		if (file == null || !file.exists()) return;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#")) continue;
				int colon = line.indexOf(":");
				if (colon <= 0) continue;
				String key = line.substring(0, colon).trim();
				String value = line.substring(colon + 1).trim();
				if (value.endsWith(";")) value = value.substring(0, value.length() - 1);
				props.setProperty(key, value);
			}
		} catch (IOException ignored) {}
	}

	public void reload() { props.clear(); load(currentFile); }

	public void setThemeFile(File file) { props.clear(); load(file); }

	public Color getColor(String key, Color fallback) {
		String v = props.getProperty(key);
		if (v == null) return fallback;
		try { return Color.decode(v); } catch (Exception e) { return fallback; }
	}

	public Font getFont(String key, Font fallback) {
		String v = props.getProperty(key);
		if (v == null) return fallback;
		try {
			String[] parts = v.split(",");
			String name = parts[0].trim();
			int style = Font.PLAIN;
			int size = fallback.getSize();
			if (parts.length >= 2) {
				String styleStr = parts[1].trim().toLowerCase(Locale.ROOT);
				if (styleStr.contains("bold")) style |= Font.BOLD;
				if (styleStr.contains("italic")) style |= Font.ITALIC;
			}
			if (parts.length >= 3) size = Integer.parseInt(parts[2].trim());
			return new Font(name, style, size);
		} catch (Exception e) {
			return fallback;
		}
	}

	public void applyTo(Component component) {
		Color bg = getColor("--bg", component.getBackground());
		Color fg = getColor("--fg", component.getForeground());
		Font font = getFont("--font", component.getFont());
		component.setBackground(bg);
		component.setForeground(fg);
		component.setFont(font);
		if (component instanceof Container) {
			for (Component child : ((Container) component).getComponents()) applyTo(child);
		}
	}
}


