package org.example.smoothies.ui.session;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.store.AppStore;

@Component
public class SessionRestore {

	public void restoreIngredientSelection(AppStore store, AppPreferences preferences) {
		if (!preferences.restoreLastSelection() || preferences.lastSelectedIngredients().isEmpty()) {
			return;
		}

		Set<String> known = Set.copyOf(store.getState().allIngredients());
		Set<String> restored = preferences.lastSelectedIngredients().stream().filter(known::contains)
				.collect(Collectors.toCollection(LinkedHashSet::new));

		if (restored.isEmpty()) {
			return;
		}
		store.dispatch(new AppMessage.IngredientsSelectionChanged(Set.copyOf(restored)));
	}
}
