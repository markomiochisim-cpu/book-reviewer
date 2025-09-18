import theme.ThemeManager;
import storage.UserStorage;
import ui.MainFrame;
import ui.RegisterFrame;
import util.Config;

import javax.swing.*;
import java.io.File;

public class BookReviewer {
    private final Config config = new Config(new File("config.properties"));
    private final ThemeManager theme = new ThemeManager(new File("theme_dark".equals(config.get("theme","theme_dark")) ? "theme_dark.css" : "theme_light.css"));
    private final UserStorage storage = new UserStorage(new File("users"));

    public BookReviewer() {
        showRegistration();
    }

    private void showRegistration() {
        new RegisterFrame(storage, theme, this::showMain);
    }

    private void showMain(String userId) {
        new MainFrame(userId, storage, theme);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BookReviewer::new);
    }
}

