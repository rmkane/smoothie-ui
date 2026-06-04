package org.example.smoothies.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppDirectories {

	public static final String APP_ID = "smoothie-maker";
	private static final String LOGS_DIR_NAME = "logs";

	/**
	 * System property set before Spring/logging starts; consumed by
	 * {@code application.yml}.
	 */
	public static final String LOG_DIR_PROPERTY = "smoothie.log.dir";

	/** Call from {@code main} before Spring Boot initializes logging. */
	public static void configureLoggingDirectory() {
		System.setProperty(LOG_DIR_PROPERTY, logsDirectory().toAbsolutePath().toString());
	}

	public static Path logsDirectory() {
		Path directory = configDirectory().resolve(LOGS_DIR_NAME);
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not create logs directory: " + directory, e);
		}
		return directory;
	}

	public static Path logFile() {
		return logsDirectory().resolve("smoothie-maker.log");
	}

	public static Path configDirectory() {
		Path directory = resolveConfigDirectory();
		try {
			Files.createDirectories(directory);
		} catch (IOException e) {
			throw new UncheckedIOException("Could not create config directory: " + directory, e);
		}
		return directory;
	}

	public static Path preferencesFile() {
		return resolveConfigDirectory().resolve("preferences.json");
	}

	static Path resolveConfigDirectory() {
		Path home = Path.of(System.getProperty("user.home"));
		String os = System.getProperty("os.name", "").toLowerCase();

		if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			if (appData != null && !appData.isBlank()) {
				return Path.of(appData, APP_ID);
			}
			return home.resolve("AppData").resolve("Roaming").resolve(APP_ID);
		}

		if (os.contains("mac")) {
			return home.resolve("Library").resolve("Application Support").resolve(APP_ID);
		}

		String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");
		if (xdgConfigHome != null && !xdgConfigHome.isBlank()) {
			return Path.of(xdgConfigHome, APP_ID);
		}

		return home.resolve(".config").resolve(APP_ID);
	}
}
