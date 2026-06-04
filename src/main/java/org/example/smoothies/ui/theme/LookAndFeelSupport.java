package org.example.smoothies.ui.theme;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatSystemProperties;

import lombok.experimental.UtilityClass;

import org.example.smoothies.config.UiTheme;

@UtilityClass
public class LookAndFeelSupport {

	public static void apply(UiTheme theme, float uiScale) {
		applyUiScale(uiScale);
		try {
			switch (theme) {
				case SYSTEM -> applySystemTheme();
				case LIGHT -> FlatLightLaf.setup();
				case DARK -> FlatDarkLaf.setup();
			}
			showMenuMnemonics();
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Could not set look and feel, using default: " + e.getMessage());
			fallbackToCrossPlatform();
			showMenuMnemonics();
		}
	}

	/**
	 * FlatLaf hides mnemonic underlines until Alt is pressed; keep them visible
	 * like classic desktop apps.
	 */
	private static void showMenuMnemonics() {
		UIManager.put("Component.hideMnemonics", false);
	}

	public static void refreshAllWindows() {
		FlatLaf.updateUI();
	}

	private static void applyUiScale(float uiScale) {
		System.setProperty(FlatSystemProperties.UI_SCALE_ENABLED, "true");
		System.setProperty(FlatSystemProperties.UI_SCALE, Float.toString(uiScale));
	}

	private static void applySystemTheme() throws UnsupportedLookAndFeelException {
		if (SystemTheme.isOsDarkTheme()) {
			FlatDarkLaf.setup();
			return;
		}
		FlatLightLaf.setup();
	}

	private static void fallbackToCrossPlatform() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			System.err.println("Could not set fallback look and feel: " + e.getMessage());
		}
	}
}
