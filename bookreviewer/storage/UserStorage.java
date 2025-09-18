package storage;

import model.User;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserStorage {
	private final File baseDir;

	public UserStorage(File baseDir) {
		this.baseDir = baseDir;
		if (!baseDir.exists()) baseDir.mkdirs();
	}

	public String register(String username, String roll, String ageStr, String email) throws IOException {
		int age = Integer.parseInt(ageStr.trim());
		User user = new User(username.trim(), roll.trim(), age, email.trim());
		File userFile = new File(baseDir, user.getUserId() + ".txt");
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(userFile), StandardCharsets.UTF_8))) {
			bw.write("Username:" + user.getUsername() + "\n");
			bw.write("Roll:" + user.getRoll() + "\n");
			bw.write("Age:" + user.getAge() + "\n");
			bw.write("Email:" + user.getEmail() + "\n");
		}
		return user.getUserId();
	}

	public boolean userExists(String userId) {
		File userFile = new File(baseDir, userId + ".txt");
		return userFile.exists();
	}

	public File getUserDataFile(String userId) {
		return new File(baseDir, userId + ".data");
	}

	public void saveUserState(String userId, Set<String> favoriteBookIds, Map<String, String> statuses, Map<String, Double> ratings) {
		File file = getUserDataFile(userId);
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			bw.write("[FAVORITES]\n");
			for (String id : favoriteBookIds) bw.write(id + "\n");
			bw.write("[STATUSES]\n");
			for (Map.Entry<String, String> e : statuses.entrySet()) bw.write(e.getKey() + "," + e.getValue() + "\n");
			bw.write("[RATINGS]\n");
			for (Map.Entry<String, Double> e : ratings.entrySet()) bw.write(e.getKey() + "," + e.getValue() + "\n");
		} catch (IOException ignored) {}
	}

	public void loadUserState(String userId, Set<String> favoriteBookIds, Map<String, String> statuses, Map<String, Double> ratings) {
		File file = getUserDataFile(userId);
		if (!file.exists()) return;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			String line;
			boolean inFav = false, inStat = false, inRate = false;
			while ((line = br.readLine()) != null) {
				if (line.equals("[FAVORITES]")) { inFav = true; inStat = false; inRate = false; continue; }
				if (line.equals("[STATUSES]")) { inFav = false; inStat = true; inRate = false; continue; }
				if (line.equals("[RATINGS]")) { inFav = false; inStat = false; inRate = true; continue; }
				if (inFav) {
					favoriteBookIds.add(line.trim());
				} else if (inStat) {
					String[] parts = line.split(",");
					if (parts.length >= 2) statuses.put(parts[0].trim(), parts[1].trim());
				} else if (inRate) {
					String[] parts = line.split(",");
					if (parts.length >= 2) {
						try { ratings.put(parts[0].trim(), Double.parseDouble(parts[1].trim())); } catch (NumberFormatException ignored) {}
					}
				}
			}
		} catch (IOException ignored) {}
	}
}


