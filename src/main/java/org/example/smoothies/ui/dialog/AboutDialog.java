package org.example.smoothies.ui.dialog;

import java.awt.*;

import javax.swing.*;

import lombok.experimental.UtilityClass;

import org.example.smoothies.ui.support.AppIcons;
import org.example.smoothies.ui.support.AppInfo;

@UtilityClass
public class AboutDialog {

	public static void show(Window parent) {
		String html = """
				<html><body style='width: 300px; font-family: sans-serif;'>
				<p style='font-size: 14px;'><b>%s</b></p>
				<p>Version %s</p>
				<p>%s</p>
				</body></html>
				""".formatted(AppInfo.NAME, AppInfo.version(), AppInfo.DESCRIPTION);

		JLabel message = new JLabel(html);
		message.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		Icon icon = AppIcons.dialogIcon();

		JOptionPane.showMessageDialog(parent, message, "About " + AppInfo.NAME, JOptionPane.INFORMATION_MESSAGE, icon);
	}
}
