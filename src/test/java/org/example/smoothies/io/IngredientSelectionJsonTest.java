package org.example.smoothies.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IngredientSelectionJsonTest {

	@TempDir
	Path tempDir;

	private IngredientSelectionJson selectionJson;

	@BeforeEach
	void setUp() {
		selectionJson = new IngredientSelectionJson();
	}

	@Test
	void writesAndReadsSelection() throws Exception {
		IngredientSelectionDocument original = IngredientSelectionDocument
				.fromSelection(Set.of("milk", "nectar", "mango"));
		Path path = tempDir.resolve("selection.json");

		selectionJson.write(path, original);
		IngredientSelectionDocument loaded = selectionJson.read(path);

		assertThat(loaded.formatVersion()).isEqualTo(IngredientSelectionDocument.CURRENT_FORMAT_VERSION);
		assertThat(loaded.selectedIngredients()).containsExactly("mango", "milk", "nectar");
		assertThat(Files.readString(path)).contains("\"formatVersion\"");
	}

	@Test
	void rejectsUnsupportedFormatVersion() throws Exception {
		Path path = tempDir.resolve("bad-version.json");
		Files.writeString(path, """
				{
				  "formatVersion": 99,
				  "selectedIngredients": ["milk"]
				}
				""");

		assertThatThrownBy(() -> selectionJson.read(path)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Unsupported format version");
	}

	@Test
	void rejectsInvalidJson() throws Exception {
		Path path = tempDir.resolve("invalid.json");
		Files.writeString(path, "{ not json");

		assertThatThrownBy(() -> selectionJson.read(path)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Invalid JSON");
	}

	@Test
	void acceptsEmptySelection() throws Exception {
		IngredientSelectionDocument document = new IngredientSelectionDocument(
				IngredientSelectionDocument.CURRENT_FORMAT_VERSION, List.of());
		Path path = tempDir.resolve("empty.json");

		selectionJson.write(path, document);

		assertThat(selectionJson.read(path).selectedIngredients()).isEmpty();
	}
}
