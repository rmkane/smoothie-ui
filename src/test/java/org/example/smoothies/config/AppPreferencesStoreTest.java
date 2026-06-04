package org.example.smoothies.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

class AppPreferencesStoreTest {

	@TempDir
	Path tempDir;

	@Test
	void returnsDefaultsWhenFileMissing() {
		AppPreferencesStore store = AppPreferencesStore.forTesting(tempDir.resolve("preferences.json"),
				JsonMappers.create());

		assertThat(store.get()).isEqualTo(AppPreferences.DEFAULTS);
	}

	@Test
	void savesAndReloadsPreferences() throws Exception {
		Path preferencesFile = tempDir.resolve("preferences.json");
		ObjectMapper mapper = JsonMappers.create();
		AppPreferencesStore store = AppPreferencesStore.forTesting(preferencesFile, mapper);

		store.save(new AppPreferences(false));

		AppPreferencesStore reloaded = AppPreferencesStore.forTesting(preferencesFile, mapper);
		assertThat(reloaded.get().useSystemLookAndFeel()).isFalse();
		assertThat(Files.readString(preferencesFile)).contains("useSystemLookAndFeel");
	}
}
