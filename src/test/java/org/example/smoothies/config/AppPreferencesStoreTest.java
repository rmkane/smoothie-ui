package org.example.smoothies.config;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

class AppPreferencesStoreTest {

	@TempDir
	Path tempDir;

	@Test
	void returnsDefaultsWhenFileMissing() {
		AppPreferencesStore store = new AppPreferencesStore(tempDir.resolve("preferences.json"));

		assertThat(store.get()).isEqualTo(AppPreferences.DEFAULTS);
	}

	@Test
	void savesAndReloadsPreferences() throws Exception {
		Path preferencesFile = tempDir.resolve("preferences.json");
		AppPreferencesStore store = new AppPreferencesStore(preferencesFile);

		store.save(new AppPreferences(false));

		AppPreferencesStore reloaded = new AppPreferencesStore(preferencesFile);
		assertThat(reloaded.get().useSystemLookAndFeel()).isFalse();
		assertThat(Files.readString(preferencesFile)).contains("useSystemLookAndFeel");
	}
}
