package org.example.smoothies.ui.frame;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.ui.menu.AppMenuBar;
import org.example.smoothies.ui.support.AppIcons;
import org.example.smoothies.ui.support.AppInfo;

/**
 * Attaches shared window chrome: icons, menu bar, title.
 */
@Component
@RequiredArgsConstructor
public class FrameChrome {

	private final AppMenuBar appMenuBar;

	public void install(JFrame frame) {
		AppIcons.install(frame);
		appMenuBar.install(frame);
		frame.setTitle(AppInfo.NAME);
	}
}
