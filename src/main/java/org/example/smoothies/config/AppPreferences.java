package org.example.smoothies.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AppPreferences(UiTheme theme) {

	public static final AppPreferences DEFAULTS = new AppPreferences(UiTheme.SYSTEM);

	@JsonCreator
	public static AppPreferences fromJson(@JsonProperty("theme") UiTheme theme,
			@JsonProperty("useSystemLookAndFeel") Boolean useSystemLookAndFeel) {
		if (theme != null) {
			return new AppPreferences(theme);
		}
		if (Boolean.FALSE.equals(useSystemLookAndFeel)) {
			return new AppPreferences(UiTheme.LIGHT);
		}
		return new AppPreferences(UiTheme.SYSTEM);
	}
}
