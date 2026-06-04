package org.example.smoothies.ui.menu.builder;

import java.util.function.UnaryOperator;

import javax.swing.JMenu;

public final class MenuBuilder {

	private final JMenu menu;

	MenuBuilder(String name, char mnemonic) {
		menu = new JMenu(name);
		if (mnemonic != '\0') {
			menu.setMnemonic(mnemonic);
		}
	}

	JMenu menu() {
		return menu;
	}

	public MenuBuilder item(String text, UnaryOperator<MenuItemBuilder> configure) {
		MenuItemBuilder itemBuilder = configure.apply(new MenuItemBuilder(text));
		menu.add(itemBuilder.item());
		return this;
	}

	public MenuBuilder separator() {
		menu.addSeparator();
		return this;
	}
}
