package org.example.smoothies.ui;

import java.awt.*;

import javax.swing.*;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;

public final class PreferencesDialog {

	private PreferencesDialog() {
	}

	public static void show(JFrame parent, AppPreferencesStore preferencesStore) {
		AppPreferences current = preferencesStore.get();

		JCheckBox systemLookAndFeel = new JCheckBox("Use system look and feel", current.useSystemLookAndFeel());
		JLabel configPath = new JLabel("<html>Settings are stored in:<br><code>%s</code></html>"
				.formatted(preferencesStore.configDirectory()));
		configPath.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		panel.add(systemLookAndFeel);
		panel.add(configPath);

		int choice = JOptionPane.showConfirmDialog(parent, panel, "Preferences", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (choice != JOptionPane.OK_OPTION) {
			return;
		}

		AppPreferences updated = new AppPreferences(systemLookAndFeel.isSelected());
		if (updated.equals(current)) {
			return;
		}

		preferencesStore.save(updated);
		LookAndFeelSupport.apply(updated.useSystemLookAndFeel());
		LookAndFeelSupport.refreshAllWindows();
	}
}
