package org.example.smoothies.ui;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import lombok.experimental.UtilityClass;

import org.example.smoothies.config.UiTheme;

@UtilityClass
public class LookAndFeelSupport {

	public static void apply(UiTheme theme) {
		try {
			switch (theme) {
				case SYSTEM -> applySystemTheme();
				case LIGHT -> FlatLightLaf.setup();
				case DARK -> FlatDarkLaf.setup();
			}
		} catch (UnsupportedLookAndFeelException e) {
			System.err.println("Could not set look and feel, using default: " + e.getMessage());
			fallbackToCrossPlatform();
		}
	}

	public static void refreshAllWindows() {
		FlatLaf.updateUI();
	}

	private static void applySystemTheme() throws UnsupportedLookAndFeelException {
		if (SystemTheme.isOsDarkTheme()) {
			FlatDarkLaf.setup();
		} else {
			FlatLightLaf.setup();
		}
	}

	private static void fallbackToCrossPlatform() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			System.err.println("Could not set fallback look and feel: " + e.getMessage());
		}
	}
}
