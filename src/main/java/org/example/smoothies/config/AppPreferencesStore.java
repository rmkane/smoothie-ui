package org.example.smoothies.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppPreferencesStore {

	private final Path preferencesFile;
	private final ObjectMapper mapper;
	private AppPreferences preferences;

	AppPreferencesStore(Path preferencesFile, ObjectMapper mapper) {
		this.preferencesFile = preferencesFile;
		this.mapper = mapper;
		this.preferences = loadOrDefault(preferencesFile, mapper);
	}

	static AppPreferencesStore forTesting(Path preferencesFile, ObjectMapper mapper) {
		return new AppPreferencesStore(preferencesFile, mapper);
	}

	public static AppPreferences bootstrap() {
		return loadOrDefault(AppDirectories.preferencesFile(), JsonMappers.create());
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
