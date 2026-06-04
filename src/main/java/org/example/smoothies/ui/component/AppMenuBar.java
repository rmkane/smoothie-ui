package org.example.smoothies.ui.component;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.ui.AboutDialog;
import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.PreferencesDialog;
import org.example.smoothies.ui.SelectionFileActions;

@Component
@RequiredArgsConstructor
public class AppMenuBar {

	private final AppStore store;
	private final SelectionFileActions selectionFileActions;
	private final AppPreferencesStore preferencesStore;

	public void install(JFrame frame) {
		// @formatter:off
		MenuBarBuilder.forFrame(frame)
				.menu("File")
					.item("Import Selection...", createImportSelectionAction(frame))
					.item("Export Selection...", createExportSelectionAction(frame))
					.separator()
					.item("Preferences...", createPreferencesAction(frame))
					.separator()
					.item("Exit", createExitAction(frame))
				.menu("Help")
					.item("About", createAboutAction(frame))
				.install();
		// @formatter:on
	}

	// Import Selection...
	private Runnable createImportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.importSelection(frame, store);
	}

	// Export Selection...
	private Runnable createExportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.exportSelection(frame, store);
	}

	// Preferences...
	private Runnable createPreferencesAction(JFrame frame) {
		return () -> PreferencesDialog.show(frame, preferencesStore);
	}

	// Exit
	private Runnable createExitAction(JFrame frame) {
		return () -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	// About
	private Runnable createAboutAction(JFrame frame) {
		return () -> AboutDialog.show(frame);
	}
}
