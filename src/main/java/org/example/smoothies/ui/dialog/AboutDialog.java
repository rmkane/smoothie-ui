package org.example.smoothies.ui.dialog;

import java.awt.*;

import javax.swing.*;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.support.AppIcons;
import org.example.smoothies.ui.support.AppInfo;

@Component
@RequiredArgsConstructor
public class AboutDialog {

	private final UiMessages messages;

	public void show(Window parent) {
		String appName = messages.get("app.name");
		String html = messages.get("about.body", appName, messages.get("app.version", AppInfo.version()),
				messages.get("app.description"));

		JLabel message = new JLabel(html);
		message.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		Icon icon = AppIcons.dialogIcon();

		JOptionPane.showMessageDialog(parent, message, messages.get("about.title", appName),
				JOptionPane.INFORMATION_MESSAGE, icon);
	}
}
