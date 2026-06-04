package org.example.smoothies.ui.menu.builder;

import java.util.function.UnaryOperator;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

public final class MenuBarBuilder {

	private final JFrame frame;
	private final JMenuBar menuBar = new JMenuBar();

	private MenuBarBuilder(JFrame frame) {
		this.frame = frame;
	}

	public static MenuBarBuilder forFrame(JFrame frame) {
		return new MenuBarBuilder(frame);
	}

	public MenuBarBuilder menu(String name, UnaryOperator<MenuBuilder> configure) {
		return menu(name, '\0', configure);
	}

	public MenuBarBuilder menu(String name, char mnemonic, UnaryOperator<MenuBuilder> configure) {
		MenuBuilder menuBuilder = configure.apply(new MenuBuilder(name, mnemonic));
		menuBar.add(menuBuilder.menu());
		return this;
	}

	public JMenuBar build() {
		return menuBar;
	}

	public void install() {
		frame.setJMenuBar(menuBar);
	}
}
