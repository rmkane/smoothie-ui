package org.example.smoothies.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.*;

import lombok.experimental.UtilityClass;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.config.UiTheme;
import org.example.smoothies.ui.desktop.DesktopFiles;
import org.example.smoothies.ui.theme.LookAndFeelSupport;

@UtilityClass
public class PreferencesDialog {

	private static final int CONTENT_WIDTH = 400;

	public static void show(JFrame parent, AppPreferencesStore preferencesStore) {
		AppPreferences current = preferencesStore.get();
		Form form = buildForm(parent, current, preferencesStore);

		JDialog dialog = new JDialog(parent, "Preferences", true);
		dialog.setLayout(new BorderLayout(0, 12));
		dialog.add(form.panel, BorderLayout.CENTER);
		dialog.add(createButtonBar(dialog, current, preferencesStore, form), BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private static Form buildForm(JFrame parent, AppPreferences current, AppPreferencesStore preferencesStore) {
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
		fitBoxWidth(themePanel);

		JCheckBox restoreSelection = new JCheckBox("Restore last ingredient selection on startup",
				current.restoreLastSelection());
		fitBoxWidth(restoreSelection);

		JComboBox<String> scaleChoices = new JComboBox<>(scaleLabels());
		scaleChoices.setSelectedIndex(indexForScale(current.uiScale()));
		fitBoxWidth(scaleChoices);

		JPanel appearancePanel = new JPanel();
		appearancePanel.setLayout(new BoxLayout(appearancePanel, BoxLayout.Y_AXIS));
		appearancePanel.setBorder(BorderFactory.createTitledBorder("Appearance"));
		JLabel scaleLabel = new JLabel("UI scale:");
		fitBoxWidth(scaleLabel);
		appearancePanel.add(scaleLabel);
		appearancePanel.add(scaleChoices);
		fitBoxWidth(appearancePanel);

		JLabel configPath = new JLabel("<html>Settings are stored in:<br><code>%s</code></html>"
				.formatted(preferencesStore.configDirectory()));
		fitBoxWidthWrapping(configPath);

		JButton openSettingsFolder = new JButton("Open in file explorer");
		openSettingsFolder
				.addActionListener(e -> DesktopFiles.openInFileManager(parent, preferencesStore.configDirectory()));
		fitBoxWidth(openSettingsFolder);

		JPanel storagePanel = new JPanel();
		storagePanel.setLayout(new BoxLayout(storagePanel, BoxLayout.Y_AXIS));
		storagePanel.setBorder(BorderFactory.createTitledBorder("Settings location"));
		storagePanel.add(configPath);
		storagePanel.add(Box.createVerticalStrut(6));
		storagePanel.add(openSettingsFolder);
		fitBoxWidth(storagePanel);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		panel.add(themePanel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(appearancePanel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(restoreSelection);
		panel.add(Box.createVerticalStrut(8));
		panel.add(storagePanel);
		fitBoxWidth(panel);

		return new Form(panel, systemTheme, lightTheme, darkTheme, restoreSelection, scaleChoices);
	}

	private static JPanel createButtonBar(JDialog dialog, AppPreferences current, AppPreferencesStore preferencesStore,
			Form form) {
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		okButton.addActionListener(e -> {
			applyChanges(current, preferencesStore, form);
			dialog.dispose();
		});
		cancelButton.addActionListener(e -> dialog.dispose());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.add(okButton);
		buttons.add(cancelButton);
		buttons.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

		dialog.getRootPane().setDefaultButton(okButton);
		dialog.getRootPane().registerKeyboardAction(e -> cancelButton.doClick(),
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

		return buttons;
	}

	private static void applyChanges(AppPreferences current, AppPreferencesStore preferencesStore, Form form) {
		UiTheme selectedTheme = selectedTheme(form.systemTheme, form.lightTheme, form.darkTheme);
		float selectedScale = AppPreferences.UI_SCALE_OPTIONS.get(form.scaleChoices.getSelectedIndex());
		AppPreferences updated = current.withDialogSettings(selectedTheme, form.restoreSelection.isSelected(),
				selectedScale);

		if (updated.equals(current)) {
			return;
		}

		preferencesStore.save(updated);
		boolean themeOrScaleChanged = updated.theme() != current.theme() || updated.uiScale() != current.uiScale();
		if (themeOrScaleChanged) {
			LookAndFeelSupport.apply(updated.theme(), updated.uiScale());
			LookAndFeelSupport.refreshAllWindows();
		}
	}

	private static void fitBoxWidth(JComponent component) {
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		int height = component.getPreferredSize().height;
		component.setMaximumSize(new Dimension(CONTENT_WIDTH, height > 0 ? height : Integer.MAX_VALUE));
	}

	private static void fitBoxWidthWrapping(JComponent component) {
		component.setAlignmentX(Component.LEFT_ALIGNMENT);
		component.setMaximumSize(new Dimension(CONTENT_WIDTH, Integer.MAX_VALUE));
	}

	private static String[] scaleLabels() {
		return AppPreferences.UI_SCALE_OPTIONS.stream().map(scale -> "%d%%".formatted(Math.round(scale * 100)))
				.toArray(String[]::new);
	}

	private static int indexForScale(float uiScale) {
		int index = AppPreferences.UI_SCALE_OPTIONS.indexOf(uiScale);
		return index >= 0 ? index : 0;
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

	private record Form(JPanel panel, JRadioButton systemTheme, JRadioButton lightTheme, JRadioButton darkTheme,
			JCheckBox restoreSelection, JComboBox<String> scaleChoices) {
	}
}
