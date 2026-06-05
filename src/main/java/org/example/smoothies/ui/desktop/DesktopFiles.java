package org.example.smoothies.ui.desktop;

import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.i18n.UiMessages;

@Component
@RequiredArgsConstructor
public class DesktopFiles {

	private final UiMessages messages;

	public void openInFileManager(java.awt.Component parent, Path directory) {
		if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
			showError(parent, messages.get("desktop.open.unsupported"));
			return;
		}

		try {
			Files.createDirectories(directory);
			Desktop.getDesktop().open(directory.toFile());
		} catch (IOException | UnsupportedOperationException e) {
			showError(parent, messages.get("desktop.open.error", directory));
		}
	}

	private void showError(java.awt.Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, messages.get("app.name"), JOptionPane.ERROR_MESSAGE);
	}
}
