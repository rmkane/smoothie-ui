package org.example.smoothies.ui;

import javax.swing.*;

import lombok.experimental.UtilityClass;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.config.UiTheme;

@UtilityClass
public class PreferencesDialog {

	public static void show(JFrame parent, AppPreferencesStore preferencesStore) {
		AppPreferences current = preferencesStore.get();

		JRadioButton systemTheme = new JRadioButton("System", current.theme() == UiTheme.SYSTEM);
		JRadioButton lightTheme = new JRadioButton("Light", current.theme() == UiTheme.LIGHT);
		JRadioButton darkTheme = new JRadioButton("Dark", current.theme() == UiTheme.DARK);

		ButtonGroup themeGroup = new ButtonGroup();
		themeGroup.add(systemTheme);
		themeGroup.add(lightTheme);
		themeGroup.add(darkTheme);

		JPanel themePanel = new JPanel();
		themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
		themePanel.setBorder(BorderFactory.createTitledBorder("Theme"));
		themePanel.add(systemTheme);
		themePanel.add(lightTheme);
		themePanel.add(darkTheme);

		JLabel configPath = new JLabel("<html>Settings are stored in:<br><code>%s</code></html>"
				.formatted(preferencesStore.configDirectory()));
		configPath.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		panel.add(themePanel);
		panel.add(configPath);

		int choice = JOptionPane.showConfirmDialog(parent, panel, "Preferences", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (choice != JOptionPane.OK_OPTION) {
			return;
		}

		UiTheme selected = selectedTheme(systemTheme, lightTheme, darkTheme);
		AppPreferences updated = new AppPreferences(selected);
		if (updated.equals(current)) {
			return;
		}

		preferencesStore.save(updated);
		LookAndFeelSupport.apply(updated.theme());
		LookAndFeelSupport.refreshAllWindows();
	}

	private static UiTheme selectedTheme(JRadioButton systemTheme, JRadioButton lightTheme, JRadioButton darkTheme) {
		if (lightTheme.isSelected()) {
			return UiTheme.LIGHT;
		}
		if (darkTheme.isSelected()) {
			return UiTheme.DARK;
		}
		return UiTheme.SYSTEM;
	}
}
