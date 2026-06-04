package org.example.smoothies.ui;

import java.awt.Window;

import javax.swing.*;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LookAndFeelSupport {

	public static void apply(boolean useSystemLookAndFeel) {
		try {
			String className = useSystemLookAndFeel
					? UIManager.getSystemLookAndFeelClassName()
					: UIManager.getCrossPlatformLookAndFeelClassName();
			UIManager.setLookAndFeel(className);
		} catch (ReflectiveOperationException | UnsupportedLookAndFeelException e) {
			System.err.println("Could not set look and feel, using default: " + e.getMessage());
		}
	}

	public static void refreshAllWindows() {
		for (Window window : Window.getWindows()) {
			SwingUtilities.updateComponentTreeUI(window);
		}
	}
}
