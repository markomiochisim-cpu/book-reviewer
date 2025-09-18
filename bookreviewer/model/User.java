package model;

import java.util.*;

public class User {
	private final String username;
	private final String roll;
	private final int age;
	private final String email;
	private final String userId; // username_roll

	// bookId -> status (Completed/Reading/Read Later)
	private final Map<String, String> bookStatuses = new HashMap<>();
	private final Set<String> favoriteBookIds = new HashSet<>();

	public User(String username, String roll, int age, String email) {
		this.username = username;
		this.roll = roll;
		this.age = age;
		this.email = email;
		this.userId = username + "_" + roll;
	}

	public String getUsername() { return username; }
	public String getRoll() { return roll; }
	public int getAge() { return age; }
	public String getEmail() { return email; }
	public String getUserId() { return userId; }

	public void setBookStatus(String bookId, String status) { bookStatuses.put(bookId, status); }
	public String getBookStatus(String bookId) { return bookStatuses.getOrDefault(bookId, ""); }

	public void addFavorite(String bookId) { favoriteBookIds.add(bookId); }
	public void removeFavorite(String bookId) { favoriteBookIds.remove(bookId); }
	public boolean isFavorite(String bookId) { return favoriteBookIds.contains(bookId); }
	public Set<String> getFavoriteBookIds() { return Collections.unmodifiableSet(favoriteBookIds); }

	public Map<String, String> getBookStatuses() { return Collections.unmodifiableMap(bookStatuses); }
}


