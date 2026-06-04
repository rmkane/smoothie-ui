package org.example.smoothies.config;

import java.awt.Rectangle;

public record WindowBounds(int x, int y, int width, int height) {

	public static WindowBounds fromRectangle(Rectangle bounds) {
		return new WindowBounds(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public Rectangle toRectangle() {
		return new Rectangle(x, y, width, height);
	}
}
