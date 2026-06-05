package org.example.smoothies.ui.file;

import java.io.File;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.io.IngredientSelectionDocument;
import org.example.smoothies.io.IngredientSelectionJson;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;
import org.example.smoothies.ui.store.AppStore;

@Slf4j
@Component
@RequiredArgsConstructor
public class SelectionFileActions {

	private static final String DEFAULT_FILENAME = "ingredient-selection.json";

	private final IngredientSelectionJson selectionJson;
	private final AppPreferencesStore preferencesStore;
	private final UiMessages messages;

	public void exportSelection(JFrame parent, AppStore store) {
		JFileChooser chooser = createChooser(messages.get("file.export.title"));
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		chooser.setSelectedFile(new File(DEFAULT_FILENAME));

		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path path = ensureJsonExtension(chooser.getSelectedFile().toPath());
		try {
			IngredientSelectionDocument document = IngredientSelectionDocument
					.fromSelection(store.getState().selectedIngredients());
			selectionJson.write(path, document);
			preferencesStore.rememberFileChooserDirectory(path);
			showInfo(parent, messages.get("file.export.success", path));
		} catch (IOException e) {
			log.error("Failed to export selection to {}", path, e);
			showError(parent, messages.get("file.export.error", e.getMessage()));
		}
	}

	public void importSelection(JFrame parent, AppStore store) {
		JFileChooser chooser = createChooser(messages.get("file.import.title"));
		chooser.setDialogType(JFileChooser.OPEN_DIALOG);

		if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		Path path = chooser.getSelectedFile().toPath();
		try {
			IngredientSelectionDocument document = selectionJson.read(path);
			ImportResult result = resolveSelection(document.selectedIngredients(), store.getState());

			store.dispatch(new AppMessage.IngredientsSelectionChanged(result.selection()));
			preferencesStore.rememberFileChooserDirectory(path);

			if (!result.unknownIngredients().isEmpty()) {
				showWarning(parent,
						messages.get("file.import.unknown", String.join(", ", result.unknownIngredients())));
				return;
			}
			showInfo(parent, messages.get("file.import.success", result.selection().size()));
		} catch (IOException e) {
			log.error("Failed to read selection from {}", path, e);
			showError(parent, messages.get("file.read.error", e.getMessage()));
		} catch (IllegalArgumentException e) {
			log.warn("Invalid selection file {}: {}", path, e.getMessage());
			showError(parent, messages.get("file.invalid", e.getMessage()));
		}
	}

	private JFileChooser createChooser(String title) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(title);
		chooser.setFileFilter(new FileNameExtensionFilter(messages.get("file.filter.json"), "json"));
		chooser.setAcceptAllFileFilterUsed(false);

		String lastDirectory = preferencesStore.get().lastFileChooserDirectory();
		if (lastDirectory == null) {
			return chooser;
		}
		File directory = new File(lastDirectory);
		if (!directory.isDirectory()) {
			return chooser;
		}
		chooser.setCurrentDirectory(directory);
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

	private void showInfo(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, messages.get("app.name"), JOptionPane.INFORMATION_MESSAGE);
	}

	private void showWarning(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, messages.get("app.name"), JOptionPane.WARNING_MESSAGE);
	}

	private void showError(JFrame parent, String message) {
		JOptionPane.showMessageDialog(parent, message, messages.get("app.name"), JOptionPane.ERROR_MESSAGE);
	}

	private record ImportResult(Set<String> selection, List<String> unknownIngredients) {
	}
}
