package org.example.smoothies.config;

public record AppPreferences(boolean useSystemLookAndFeel) {

	public static final AppPreferences DEFAULTS = new AppPreferences(true);
}
