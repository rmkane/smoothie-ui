package org.example.smoothies.ui.menu;

import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public final class MenuBarBuilder {

	private final JFrame frame;
	private final JMenuBar menuBar = new JMenuBar();
	private JMenu currentMenu;

	private MenuBarBuilder(JFrame frame) {
		this.frame = frame;
	}

	public static MenuBarBuilder forFrame(JFrame frame) {
		return new MenuBarBuilder(frame);
	}

	public MenuBarBuilder menu(String name) {
		return menu(name, '\0');
	}

	public MenuBarBuilder menu(String name, char mnemonic) {
		currentMenu = new JMenu(name);
		if (mnemonic != '\0') {
			currentMenu.setMnemonic(mnemonic);
		}
		menuBar.add(currentMenu);
		return this;
	}

	public MenuBarBuilder item(String text, Runnable action) {
		return item(text, '\0', -1, action);
	}

	public MenuBarBuilder item(String text, char mnemonic, Runnable action) {
		return item(text, mnemonic, -1, action);
	}

	public MenuBarBuilder item(String text, char mnemonic, int displayedMnemonicIndex, Runnable action) {
		return item(text, mnemonic, displayedMnemonicIndex, e -> action.run());
	}

	public MenuBarBuilder item(String text, ActionListener action) {
		return item(text, '\0', -1, action);
	}

	public MenuBarBuilder item(String text, char mnemonic, ActionListener action) {
		return item(text, mnemonic, -1, action);
	}

	public MenuBarBuilder item(String text, char mnemonic, int displayedMnemonicIndex, ActionListener action) {
		requireMenu();
		JMenuItem item = new JMenuItem(text);
		if (mnemonic != '\0') {
			item.setMnemonic(mnemonic);
			if (displayedMnemonicIndex >= 0) {
				item.setDisplayedMnemonicIndex(displayedMnemonicIndex);
			}
		}
		item.addActionListener(action);
		currentMenu.add(item);
		return this;
	}

	public MenuBarBuilder separator() {
		requireMenu();
		currentMenu.addSeparator();
		return this;
	}

	public JMenuBar build() {
		return menuBar;
	}

	public void install() {
		frame.setJMenuBar(menuBar);
	}

	private void requireMenu() {
		if (currentMenu == null) {
			throw new IllegalStateException("Call menu() before adding items");
		}
	}
}
