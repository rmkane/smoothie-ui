package org.example.smoothies.ui;

import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public final class AppIcons {

	private static final int[] SIZES = {16, 32, 48, 64, 128, 256};

	private AppIcons() {
	}

	public static void applyTo(JFrame frame) {
		List<Image> images = loadIconImages();
		if (images.isEmpty()) {
			return;
		}
		frame.setIconImages(images);
		if (Taskbar.isTaskbarSupported()) {
			Taskbar.getTaskbar().setIconImage(images.get(images.size() - 1));
		}
	}

	private static List<Image> loadIconImages() {
		List<Image> images = new ArrayList<>();
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		for (int size : SIZES) {
			URL url = AppIcons.class.getResource("/icons/icon-" + size + ".png");
			if (url != null) {
				images.add(toolkit.getImage(url));
			}
		}
		return images;
	}
}
