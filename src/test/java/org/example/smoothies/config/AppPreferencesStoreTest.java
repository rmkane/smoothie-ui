package org.example.smoothies.config;

import java.awt.Rectangle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

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
				JsonMappers.createJson());

		assertThat(store.get()).isEqualTo(AppPreferences.DEFAULTS);
	}

	@Test
	void savesAndReloadsPreferences() throws Exception {
		Path preferencesFile = tempDir.resolve("preferences.json");
		ObjectMapper mapper = JsonMappers.createJson();
		AppPreferencesStore store = AppPreferencesStore.forTesting(preferencesFile, mapper);

		store.save(new AppPreferences(UiTheme.DARK, false, List.of("milk"), null, "/tmp", 1.25f, null));

		AppPreferencesStore reloaded = AppPreferencesStore.forTesting(preferencesFile, mapper);
		assertThat(reloaded.get().theme()).isEqualTo(UiTheme.DARK);
		assertThat(reloaded.get().uiScale()).isEqualTo(1.25f);
		assertThat(reloaded.get().lastSelectedIngredients()).containsExactly("milk");
		assertThat(Files.readString(preferencesFile)).contains("\"theme\"");
	}

	@Test
	void migratesLegacyUseSystemLookAndFeel() throws Exception {
		Path preferencesFile = tempDir.resolve("preferences.json");
		Files.writeString(preferencesFile, """
				{
				  "useSystemLookAndFeel": false
				}
				""");

		AppPreferencesStore store = AppPreferencesStore.forTesting(preferencesFile, JsonMappers.createJson());

		assertThat(store.get().theme()).isEqualTo(UiTheme.LIGHT);
		assertThat(store.get().restoreLastSelection()).isTrue();
	}

	@Test
	void saveSessionPersistsSelectionAndWindowBounds() {
		Path preferencesFile = tempDir.resolve("preferences.json");
		AppPreferencesStore store = AppPreferencesStore.forTesting(preferencesFile, JsonMappers.createJson());

		store.saveSession(Set.of("nectar", "milk"), new Rectangle(10, 20, 800, 600));

		assertThat(store.get().lastSelectedIngredients()).containsExactly("milk", "nectar");
		assertThat(store.get().windowBounds()).isEqualTo(new WindowBounds(10, 20, 800, 600));
	}

	@Test
	void rememberFileChooserDirectory() {
		Path preferencesFile = tempDir.resolve("preferences.json");
		AppPreferencesStore store = AppPreferencesStore.forTesting(preferencesFile, JsonMappers.createJson());

		store.rememberFileChooserDirectory(tempDir.resolve("exports/selection.json"));

		assertThat(store.get().lastFileChooserDirectory())
				.isEqualTo(tempDir.resolve("exports").toAbsolutePath().toString());
	}
}
