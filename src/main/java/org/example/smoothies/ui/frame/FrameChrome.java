package org.example.smoothies.ui.frame;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.menu.AppMenuBar;
import org.example.smoothies.ui.support.AppIcons;

/**
 * Attaches shared window chrome: icons, menu bar, title.
 */
@Component
@RequiredArgsConstructor
public class FrameChrome {

	private final AppMenuBar appMenuBar;
	private final UiMessages messages;

	public void install(JFrame frame) {
		AppIcons.install(frame);
		appMenuBar.install(frame);
		frame.setTitle(messages.get("app.name"));
	}
}
