package org.example.smoothies.io;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"formatVersion", "selectedIngredients"})
public record IngredientSelectionDocument(int formatVersion, List<String> selectedIngredients) {

	public static final int CURRENT_FORMAT_VERSION = 1;

	public static IngredientSelectionDocument fromSelection(Set<String> selected) {
		List<String> sorted = selected.stream().sorted().toList();
		return new IngredientSelectionDocument(CURRENT_FORMAT_VERSION, sorted);
	}
}
