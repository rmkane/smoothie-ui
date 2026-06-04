package org.example.smoothies.config;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppPreferencesStore {

	private final Path preferencesFile;
	private final ObjectMapper mapper;
	private AppPreferences preferences;

	public AppPreferencesStore(Path preferencesFile, ObjectMapper mapper) {
		this.preferencesFile = preferencesFile;
		this.mapper = mapper;
		this.preferences = loadOrDefault(preferencesFile, mapper);
	}

	static AppPreferencesStore forTesting(Path preferencesFile, ObjectMapper mapper) {
		return new AppPreferencesStore(preferencesFile, mapper);
	}

	public static AppPreferences bootstrap() {
		return loadOrDefault(AppDirectories.preferencesFile(), JsonMappers.createJson());
	}

	public AppPreferences get() {
		return preferences;
	}

	public Path configDirectory() {
		return preferencesFile.getParent();
	}

	public void save(AppPreferences updated) {
		try {
			Files.createDirectories(preferencesFile.getParent());
			mapper.writeValue(preferencesFile.toFile(), updated);
			this.preferences = updated;
			log.info("Saved preferences to {}", preferencesFile);
		} catch (IOException e) {
			throw new IllegalStateException("Could not save preferences to " + preferencesFile, e);
		}
	}

	public void saveSession(Set<String> selectedIngredients, Rectangle windowBounds) {
		save(preferences.withSession(selectedIngredients, WindowBounds.fromRectangle(windowBounds)));
	}

	public void rememberFileChooserDirectory(Path path) {
		if (path == null) {
			return;
		}
		Path directory = Files.isDirectory(path) ? path : path.getParent();
		if (directory == null) {
			return;
		}
		String directoryString = directory.toAbsolutePath().toString();
		if (directoryString.equals(preferences.lastFileChooserDirectory())) {
			return;
		}
		save(preferences.withLastFileChooserDirectory(directoryString));
	}

	private static AppPreferences loadOrDefault(Path preferencesFile, ObjectMapper mapper) {
		if (!Files.isRegularFile(preferencesFile)) {
			return AppPreferences.DEFAULTS;
		}
		try {
			AppPreferences loaded = mapper.readValue(preferencesFile.toFile(), AppPreferences.class);
			if (loaded == null) {
				return AppPreferences.DEFAULTS;
			}
			log.debug("Loaded preferences from {}", preferencesFile);
			return loaded;
		} catch (IOException e) {
			log.warn("Could not read preferences from {}, using defaults: {}", preferencesFile, e.getMessage());
			return AppPreferences.DEFAULTS;
		}
	}
}
