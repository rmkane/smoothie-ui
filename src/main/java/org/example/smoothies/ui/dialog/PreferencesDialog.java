package org.example.smoothies.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.config.UiTheme;
import org.example.smoothies.i18n.AppLocales;
import org.example.smoothies.i18n.UiLocaleCoordinator;
import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.desktop.DesktopFiles;
import org.example.smoothies.ui.theme.LookAndFeelSupport;

@Component
@RequiredArgsConstructor
public class PreferencesDialog {

	private static final int CONTENT_WIDTH = 400;

	private final AppPreferencesStore preferencesStore;
	private final DesktopFiles desktopFiles;
	private final UiMessages messages;
	private final ObjectProvider<UiLocaleCoordinator> localeCoordinator;

	public void show(JFrame parent) {
		AppPreferences current = preferencesStore.get();
		Form form = buildForm(parent, current);

		JDialog dialog = new JDialog(parent, messages.get("prefs.title"), true);
		dialog.setLayout(new BorderLayout(0, 12));
		dialog.add(form.panel, BorderLayout.CENTER);
		dialog.add(createButtonBar(dialog, parent, current, form), BorderLayout.SOUTH);
		dialog.pack();
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private Form buildForm(JFrame parent, AppPreferences current) {
		JRadioButton systemTheme = new JRadioButton(messages.get("prefs.theme.system"),
				current.theme() == UiTheme.SYSTEM);
		JRadioButton lightTheme = new JRadioButton(messages.get("prefs.theme.light"), current.theme() == UiTheme.LIGHT);
		JRadioButton darkTheme = new JRadioButton(messages.get("prefs.theme.dark"), current.theme() == UiTheme.DARK);

		ButtonGroup themeGroup = new ButtonGroup();
		themeGroup.add(systemTheme);
		themeGroup.add(lightTheme);
		themeGroup.add(darkTheme);

		JPanel themePanel = new JPanel();
		themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
		themePanel.setBorder(BorderFactory.createTitledBorder(messages.get("prefs.theme")));
		themePanel.add(systemTheme);
		themePanel.add(lightTheme);
		themePanel.add(darkTheme);
		fitBoxWidth(themePanel);

		JCheckBox restoreSelection = new JCheckBox(messages.get("prefs.restoreSelection"),
				current.restoreLastSelection());
		fitBoxWidth(restoreSelection);

		JComboBox<String> scaleChoices = new JComboBox<>(scaleLabels());
		scaleChoices.setSelectedIndex(indexForScale(current.uiScale()));
		fitBoxWidth(scaleChoices);

		JPanel appearancePanel = new JPanel();
		appearancePanel.setLayout(new BoxLayout(appearancePanel, BoxLayout.Y_AXIS));
		appearancePanel.setBorder(BorderFactory.createTitledBorder(messages.get("prefs.appearance")));
		JLabel scaleLabel = new JLabel(messages.get("prefs.scale"));
		fitBoxWidth(scaleLabel);
		appearancePanel.add(scaleLabel);
		appearancePanel.add(scaleChoices);
		fitBoxWidth(appearancePanel);

		LocaleChoice[] localeChoices = localeChoices();
		JComboBox<LocaleChoice> languageChoices = new JComboBox<>(localeChoices);
		languageChoices.setSelectedItem(selectedLocaleChoice(localeChoices, current.localeTag()));
		fitBoxWidth(languageChoices);

		JPanel languagePanel = new JPanel();
		languagePanel.setLayout(new BoxLayout(languagePanel, BoxLayout.Y_AXIS));
		languagePanel.setBorder(BorderFactory.createTitledBorder(messages.get("prefs.language")));
		languagePanel.add(languageChoices);
		fitBoxWidth(languagePanel);

		JLabel configPath = new JLabel(messages.get("prefs.storage.path", preferencesStore.configDirectory()));
		fitBoxWidthWrapping(configPath);

		JButton openSettingsFolder = new JButton(messages.get("prefs.openFolder"));
		openSettingsFolder
				.addActionListener(e -> desktopFiles.openInFileManager(parent, preferencesStore.configDirectory()));
		fitBoxWidth(openSettingsFolder);

		JPanel storagePanel = new JPanel();
		storagePanel.setLayout(new BoxLayout(storagePanel, BoxLayout.Y_AXIS));
		storagePanel.setBorder(BorderFactory.createTitledBorder(messages.get("prefs.storage")));
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
		panel.add(languagePanel);
		panel.add(Box.createVerticalStrut(8));
		panel.add(restoreSelection);
		panel.add(Box.createVerticalStrut(8));
		panel.add(storagePanel);
		fitBoxWidth(panel);

		return new Form(panel, systemTheme, lightTheme, darkTheme, restoreSelection, scaleChoices, languageChoices);
	}

	private JPanel createButtonBar(JDialog dialog, JFrame parent, AppPreferences current, Form form) {
		JButton okButton = new JButton(messages.get("button.ok"));
		JButton cancelButton = new JButton(messages.get("button.cancel"));

		okButton.addActionListener(e -> {
			applyChanges(parent, current, form);
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

	private void applyChanges(JFrame parent, AppPreferences current, Form form) {
		UiTheme selectedTheme = selectedTheme(form.systemTheme, form.lightTheme, form.darkTheme);
		float selectedScale = AppPreferences.UI_SCALE_OPTIONS.get(form.scaleChoices.getSelectedIndex());
		String selectedLocaleTag = form.languageChoices.getItemAt(form.languageChoices.getSelectedIndex()).tag();

		AppPreferences updated = current.withDialogSettings(selectedTheme, form.restoreSelection.isSelected(),
				selectedScale, selectedLocaleTag);

		if (updated.equals(current)) {
			return;
		}

		preferencesStore.save(updated);

		boolean localeChanged = !Objects.equals(updated.localeTag(), current.localeTag());
		if (localeChanged) {
			localeCoordinator.getObject().refresh(parent);
		}

		boolean themeOrScaleChanged = updated.theme() != current.theme() || updated.uiScale() != current.uiScale();
		if (themeOrScaleChanged) {
			LookAndFeelSupport.apply(updated.theme(), updated.uiScale());
			LookAndFeelSupport.refreshAllWindows();
		}
	}

	private static void fitBoxWidth(JComponent component) {
		component.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		int height = component.getPreferredSize().height;
		component.setMaximumSize(new Dimension(CONTENT_WIDTH, height > 0 ? height : Integer.MAX_VALUE));
	}

	private static void fitBoxWidthWrapping(JComponent component) {
		component.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		component.setMaximumSize(new Dimension(CONTENT_WIDTH, Integer.MAX_VALUE));
	}

	private String[] scaleLabels() {
		return AppPreferences.UI_SCALE_OPTIONS.stream()
				.map(scale -> messages.get("prefs.scale.percent", Math.round(scale * 100))).toArray(String[]::new);
	}

	private LocaleChoice[] localeChoices() {
		Map<String, String> choices = new LinkedHashMap<>();
		choices.put(null, messages.get("prefs.locale.system"));
		choices.put(AppLocales.ENGLISH_TAG, messages.get("prefs.locale.en"));
		choices.put(AppLocales.SPANISH_TAG, messages.get("prefs.locale.es"));
		return choices.entrySet().stream().map(e -> new LocaleChoice(e.getKey(), e.getValue()))
				.toArray(LocaleChoice[]::new);
	}

	private static LocaleChoice selectedLocaleChoice(LocaleChoice[] choices, String localeTag) {
		for (LocaleChoice choice : choices) {
			if (Objects.equals(choice.tag(), localeTag)) {
				return choice;
			}
		}
		return choices[0];
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

	private record LocaleChoice(String tag, String label) {
		@Override
		public String toString() {
			return label;
		}
	}

	private record Form(JPanel panel, JRadioButton systemTheme, JRadioButton lightTheme, JRadioButton darkTheme,
			JCheckBox restoreSelection, JComboBox<String> scaleChoices, JComboBox<LocaleChoice> languageChoices) {
	}
}
