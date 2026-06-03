package org.example.smoothies.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.io.IngredientSelectionDocument;
import org.example.smoothies.io.IngredientSelectionJson;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;

@Slf4j
@Component
public class SelectionFileActions {

	private static final String DEFAULT_FILENAME = "ingredient-selection.json";

	private final IngredientSelectionJson selectionJson;

	public SelectionFileActions(IngredientSelectionJson selectionJson) {
		this.selectionJson = selectionJson;
	}

	public void exportSelection(JFrame parent, AppStore store) {
		JFileChooser chooser = createChooser("Export ingredient selection");
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setSelectedFile(new java.io.File(DEFAULT_FILENAME));

		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path path = ensureJsonExtension(chooser.getSelectedFile().toPath());
		try {
			IngredientSelectionDocument document = IngredientSelectionDocument
					.fromSelection(store.getState().selectedIngredients());
			selectionJson.write(path, document);
			showInfo(parent, "Exported selection to:\n" + path);
		} catch (IOException e) {
			log.error("Failed to export selection to {}", path, e);
			showError(parent, "Could not export selection:\n" + e.getMessage());
		}
	}

	public void importSelection(JFrame parent, AppStore store) {
		JFileChooser chooser = createChooser("Import ingredient selection");
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);

		if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path path = chooser.getSelectedFile().toPath();
		try {
			IngredientSelectionDocument document = selectionJson.read(path);
			ImportResult result = resolveSelection(document.selectedIngredients(), store.getState());

			store.dispatch(new AppMessage.IngredientsSelectionChanged(result.selection()));

			if (!result.unknownIngredients().isEmpty()) {
				showWarning(parent, "Ignored unknown ingredients:\n" + String.join(", ", result.unknownIngredients()));
			} else {
				showInfo(parent, "Imported %d ingredient(s).".formatted(result.selection().size()));
			}
		} catch (IOException e) {
			log.error("Failed to read selection from {}", path, e);
			showError(parent, "Could not read file:\n" + e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("Invalid selection file {}: {}", path, e.getMessage());
			showError(parent, "Invalid selection file:\n" + e.getMessage());
		}
	}

	private static JFileChooser createChooser(String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(title);
		chooser.setFileFilter(new FileNameExtensionFilter("JSON files (*.json)", "json"));
		chooser.setAcceptAllFileFilterUsed(false);
		return chooser;
	}

	private static Path ensureJsonExtension(Path path) {
		String filename = path.getFileName().toString();
		if (filename.toLowerCase().endsWith(".json")) {
			return path;
		}
		return path.resolveSibling(filename + ".json");
	}

	private static ImportResult resolveSelection(List<String> imported, AppState state) {
		Set<String> known = Set.copyOf(state.allIngredients());
		Set<String> selection = new LinkedHashSet<>();
		List<String> unknown = new ArrayList<>();

		for (String ingredient : imported) {
			if (known.contains(ingredient)) {
				selection.add(ingredient);
			} else {
				unknown.add(ingredient);
			}
		}

		return new ImportResult(Set.copyOf(selection), unknown.stream().sorted().collect(Collectors.toList()));
	}

	private static void showInfo(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, AppInfo.NAME, JOptionPane.INFORMATION_MESSAGE);
	}

	private static void showWarning(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, AppInfo.NAME, JOptionPane.WARNING_MESSAGE);
	}

	private static void showError(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, AppInfo.NAME, JOptionPane.ERROR_MESSAGE);
	}

	private record ImportResult(Set<String> selection, List<String> unknownIngredients) {
	}
}
