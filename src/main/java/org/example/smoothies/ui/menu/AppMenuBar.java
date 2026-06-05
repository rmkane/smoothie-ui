package org.example.smoothies.ui.menu;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.i18n.UiMessages;
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
	private final PreferencesDialog preferencesDialog;
	private final AboutDialog aboutDialog;
	private final UiMessages messages;

	public void install(JFrame frame) {
		// @formatter:off
		JMenuBar menuBar = MenuBarBuilder.create()
				.menu(messages.get("menu.file"), file -> file
						.mnemonic(messages.mnemonic("menu.file.mnemonic"))
						.item(messages.get("menu.file.import"), item -> item
								.mnemonic(messages.mnemonic("menu.file.import.mnemonic"))
								.onClick(createImportSelectionAction(frame)))
						.item(messages.get("menu.file.export"), item -> item
								.mnemonic(messages.mnemonic("menu.file.export.mnemonic"))
								.onClick(createExportSelectionAction(frame)))
						.separator()
						.item(messages.get("menu.file.preferences"), item -> item
								.mnemonic(messages.mnemonic("menu.file.preferences.mnemonic"))
								.onClick(createPreferencesAction(frame)))
						.separator()
						.item(messages.get("menu.file.exit"), item -> item
								.mnemonic(messages.mnemonic("menu.file.exit.mnemonic"))
								.displayedMnemonicIndex(messages.mnemonicIndex("menu.file.exit.mnemonicIndex"))
								.onClick(createExitAction(frame))))
				.menu(messages.get("menu.help"), help -> help
						.mnemonic(messages.mnemonic("menu.help.mnemonic"))
						.item(messages.get("menu.help.about"), item -> item
								.mnemonic(messages.mnemonic("menu.help.about.mnemonic"))
								.onClick(createAboutAction(frame))))
				.build();
		// @formatter:on

		frame.setJMenuBar(menuBar);
	}

	private Runnable createImportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.importSelection(frame, store);
	}

	private Runnable createExportSelectionAction(JFrame frame) {
		return () -> selectionFileActions.exportSelection(frame, store);
	}

	private Runnable createPreferencesAction(JFrame frame) {
		return () -> preferencesDialog.show(frame);
	}

	private Runnable createExitAction(JFrame frame) {
		return () -> frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	private Runnable createAboutAction(JFrame frame) {
		return () -> aboutDialog.show(frame);
	}
}
