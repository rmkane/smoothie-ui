package org.example.smoothies.ui.component;

import javax.swing.*;

import org.springframework.stereotype.Component;

import org.example.smoothies.ui.AboutDialog;
import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.SelectionFileActions;

@Component
public class AppMenuBar {

	private final AppStore store;
	private final SelectionFileActions selectionFileActions;

	public AppMenuBar(AppStore store, SelectionFileActions selectionFileActions) {
		this.store = store;
		this.selectionFileActions = selectionFileActions;
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
		menuBar.add(fileMenu);

		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(e -> AboutDialog.show(frame));
		helpMenu.add(aboutItem);

		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
	}
}
