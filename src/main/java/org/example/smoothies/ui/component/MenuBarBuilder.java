package org.example.smoothies.ui.component;

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
		currentMenu = new JMenu(name);
		menuBar.add(currentMenu);
		return this;
	}

	public MenuBarBuilder item(String text, Runnable action) {
		return item(text, e -> action.run());
	}

	public MenuBarBuilder item(String text, ActionListener action) {
		requireMenu();
		JMenuItem item = new JMenuItem(text);
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
