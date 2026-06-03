package org.example.smoothies.ui.message;

import java.util.Set;

public sealed interface AppMessage {

	record IngredientsSelectionChanged(Set<String> selected) implements AppMessage {
	}

	record SelectAllIngredients() implements AppMessage {
	}

	record ClearAllIngredients() implements AppMessage {
	}

	record LogReportRequested() implements AppMessage {
	}
}
