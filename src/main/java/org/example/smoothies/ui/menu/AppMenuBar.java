package org.example.smoothies.ui.menu;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.ui.dialog.AboutDialog;
import org.example.smoothies.ui.dialog.PreferencesDialog;
import org.example.smoothies.ui.file.SelectionFileActions;
import org.example.smoothies.ui.menu.builder.MenuBarBuilder;
import org.example.smoothies.ui.store.AppStore;

@Component
@RequiredArgsConstructor
public class AppMenuBar {

	private final AppStore store;
	private final SelectionFileActions selectionFileActions;
	private final AppPreferencesStore preferencesStore;

	public void install(JFrame frame) {
		// @formatter:off
		JMenuBar menuBar = MenuBarBuilder.create()
				.menu("File", file -> file
						.mnemonic('F')
						.item("Import Selection...", item -> item
								.mnemonic('I')
								.onClick(createImportSelectionAction(frame)))
						.item("Export Selection...", item -> item
								.mnemonic('E')
								.onClick(createExportSelectionAction(frame)))
						.separator()
						.item("Preferences...", item -> item
								.mnemonic('P')
								.onClick(createPreferencesAction(frame)))
						.separator()
						.item("Exit", item -> item
								.mnemonic('x')
								.displayedMnemonicIndex(1)
								.onClick(createExitAction(frame))))
				.menu("Help", help -> help
						.mnemonic('H')
						.item("About", item -> item
								.mnemonic('A')
								.onClick(createAboutAction(frame))))
				.build();
		// @formatter:on

		frame.setJMenuBar(menuBar);
	}

	// Action: File > Import Selection...
	private Runnable createImportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.importSelection(frame, store);
	}

	// Action: File > Export Selection...
	private Runnable createExportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.exportSelection(frame, store);
	}

	// Action: File > Preferences...
	private Runnable createPreferencesAction(JFrame frame) {
		return () -> PreferencesDialog.show(frame, preferencesStore);
	}

	// Action: File > Exit
	private Runnable createExitAction(JFrame frame) {
		return () -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	// Action: Help > About
	private Runnable createAboutAction(JFrame frame) {
		return () -> AboutDialog.show(frame);
	}
}
