package org.example.smoothies.config;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AppPreferences(UiTheme theme, boolean restoreLastSelection, List<String> lastSelectedIngredients,
		WindowBounds windowBounds, String lastFileChooserDirectory, float uiScale) {

	public static final AppPreferences DEFAULTS = new AppPreferences(UiTheme.SYSTEM, true, List.of(), null, null, 1.0f);

	public static final List<Float> UI_SCALE_OPTIONS = List.of(1.0f, 1.25f, 1.5f);

	public AppPreferences {
		if (theme == null) {
			theme = UiTheme.SYSTEM;
		}
		if (lastSelectedIngredients == null) {
			lastSelectedIngredients = List.of();
		}
		if (uiScale <= 0) {
			uiScale = 1.0f;
		}
		lastSelectedIngredients = List.copyOf(lastSelectedIngredients);
	}

	@JsonCreator
	public static AppPreferences fromJson(@JsonProperty("theme") UiTheme theme,
			@JsonProperty("useSystemLookAndFeel") Boolean useSystemLookAndFeel,
			@JsonProperty("restoreLastSelection") Boolean restoreLastSelection,
			@JsonProperty("lastSelectedIngredients") List<String> lastSelectedIngredients,
			@JsonProperty("windowBounds") WindowBounds windowBounds,
			@JsonProperty("lastFileChooserDirectory") String lastFileChooserDirectory,
			@JsonProperty("uiScale") Float uiScale) {
		UiTheme resolvedTheme = theme;
		if (resolvedTheme == null) {
			resolvedTheme = Boolean.FALSE.equals(useSystemLookAndFeel) ? UiTheme.LIGHT : UiTheme.SYSTEM;
		}
		boolean restore = restoreLastSelection != null ? restoreLastSelection : true;
		float scale = uiScale != null && uiScale > 0 ? uiScale : 1.0f;
		return new AppPreferences(resolvedTheme, restore, lastSelectedIngredients, windowBounds,
				lastFileChooserDirectory, scale);
	}

	public AppPreferences withDialogSettings(UiTheme theme, boolean restoreLastSelection, float uiScale) {
		return new AppPreferences(theme, restoreLastSelection, lastSelectedIngredients, windowBounds,
				lastFileChooserDirectory, uiScale);
	}

	public AppPreferences withSession(Set<String> selected, WindowBounds windowBounds) {
		List<String> sorted = selected.stream().sorted().collect(Collectors.toList());
		return new AppPreferences(theme, restoreLastSelection, sorted, windowBounds, lastFileChooserDirectory, uiScale);
	}

	public AppPreferences withLastFileChooserDirectory(String directory) {
		return new AppPreferences(theme, restoreLastSelection, lastSelectedIngredients, windowBounds, directory,
				uiScale);
	}
}
