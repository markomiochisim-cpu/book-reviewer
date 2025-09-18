package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Config {
	private final File file;
	private final Properties props = new Properties();

	public Config(File file) {
		this.file = file;
		load();
	}

	private void load() {
		if (!file.exists()) return;
		try (InputStream in = new FileInputStream(file)) { props.load(in);} catch (IOException ignored) {}
	}

	public void set(String key, String value) { props.setProperty(key, value); save(); }
	public String get(String key, String def) { return props.getProperty(key, def); }

	private void save() {
		try (OutputStream out = new FileOutputStream(file)) { props.store(out, "BookReviewer config"); } catch (IOException ignored) {}
	}
}


