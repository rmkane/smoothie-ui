package org.example.smoothies.ui.menu.builder;

import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public final class MenuItemBuilder {

	private final JMenuItem item;

	MenuItemBuilder(String text) {
		item = new JMenuItem(text);
	}

	public MenuItemBuilder mnemonic(char mnemonic) {
		return mnemonic(mnemonic, -1);
	}

	public MenuItemBuilder mnemonic(char mnemonic, int displayedMnemonicIndex) {
		if (mnemonic != '\0') {
			item.setMnemonic(mnemonic);
			if (displayedMnemonicIndex >= 0) {
				item.setDisplayedMnemonicIndex(displayedMnemonicIndex);
			}
		}
		return this;
	}

	public MenuItemBuilder onClick(Runnable action) {
		return onClick(e -> action.run());
	}

	public MenuItemBuilder onClick(ActionListener action) {
		item.addActionListener(action);
		return this;
	}

	JMenuItem item() {
		return item;
	}
}
