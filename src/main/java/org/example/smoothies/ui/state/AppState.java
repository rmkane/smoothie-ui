package org.example.smoothies.ui.state;

import java.util.List;
import java.util.Set;

import org.example.smoothies.model.Smoothie;

public record AppState(List<Smoothie> recipes, List<String> allIngredients, Set<String> selectedIngredients,
		List<String> resultLines, String countLabel, String selectedSummary) {

	public static final String EMPTY_RESULTS_MESSAGE = "No smoothies match — select required and optional ingredients.";
}
