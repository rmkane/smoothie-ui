package org.example.smoothies.ui.desktop;

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import lombok.experimental.UtilityClass;

import org.example.smoothies.ui.support.AppInfo;

@UtilityClass
public class DesktopFiles {

	public static void openInFileManager(Component parent, Path directory) {
		if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
			showError(parent, "Opening folders is not supported on this system.");
			return;
		}

		try {
			Files.createDirectories(directory);
			Desktop.getDesktop().open(directory.toFile());
		} catch (IOException | UnsupportedOperationException e) {
			showError(parent, "Could not open settings folder:\n%s".formatted(directory));
		}
	}

	private static void showError(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, AppInfo.NAME, JOptionPane.ERROR_MESSAGE);
	}
}
