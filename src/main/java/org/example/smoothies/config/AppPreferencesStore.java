package org.example.smoothies.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AppPreferencesStore {

	private final Path preferencesFile;
	private final ObjectMapper mapper;
	private AppPreferences preferences;

	public AppPreferencesStore() {
		this(AppDirectories.preferencesFile());
	}

	AppPreferencesStore(Path preferencesFile) {
		this.preferencesFile = preferencesFile;
		this.mapper = new ObjectMapper();
		this.preferences = loadOrDefault(preferencesFile);
	}

	public static AppPreferences bootstrap() {
		return loadOrDefault(AppDirectories.preferencesFile());
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
			mapper.writerWithDefaultPrettyPrinter().writeValue(preferencesFile.toFile(), updated);
			this.preferences = updated;
			log.info("Saved preferences to {}", preferencesFile);
		} catch (IOException e) {
			throw new IllegalStateException("Could not save preferences to " + preferencesFile, e);
		}
	}

	private static AppPreferences loadOrDefault(Path preferencesFile) {
		if (!Files.isRegularFile(preferencesFile)) {
			return AppPreferences.DEFAULTS;
		}
		try {
			AppPreferences loaded = new ObjectMapper().readValue(preferencesFile.toFile(), AppPreferences.class);
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
