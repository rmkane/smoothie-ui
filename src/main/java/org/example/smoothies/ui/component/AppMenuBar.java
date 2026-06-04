package org.example.smoothies.ui.component;

import javax.swing.*;

import org.springframework.stereotype.Component;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.ui.AboutDialog;
import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.PreferencesDialog;
import org.example.smoothies.ui.SelectionFileActions;

@Component
public class AppMenuBar {

	private final AppStore store;
	private final SelectionFileActions selectionFileActions;
	private final AppPreferencesStore preferencesStore;

	public AppMenuBar(AppStore store, SelectionFileActions selectionFileActions, AppPreferencesStore preferencesStore) {
		this.store = store;
		this.selectionFileActions = selectionFileActions;
		this.preferencesStore = preferencesStore;
	}

	public void install(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenuItem importItem = new JMenuItem("Import Selection...");
		importItem.addActionListener(e -> selectionFileActions.importSelection(frame, store));
		JMenuItem exportItem = new JMenuItem("Export Selection...");
		exportItem.addActionListener(e -> selectionFileActions.exportSelection(frame, store));
		fileMenu.add(importItem);
		fileMenu.add(exportItem);
		fileMenu.addSeparator();
		JMenuItem preferencesItem = new JMenuItem("Preferences...");
		preferencesItem.addActionListener(e -> PreferencesDialog.show(frame, preferencesStore));
		fileMenu.add(preferencesItem);
		menuBar.add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(e -> AboutDialog.show(frame));
		helpMenu.add(aboutItem);

		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
	}
}
