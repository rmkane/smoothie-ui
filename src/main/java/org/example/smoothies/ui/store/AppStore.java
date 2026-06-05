package org.example.smoothies.ui.store;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.swing.*;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.repository.SmoothieRepository;
import org.example.smoothies.service.SmoothieService;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;
import org.example.smoothies.util.CountLabels;

@Slf4j
@Component
public class AppStore {

	private final SmoothieService smoothieService;
	private final UiMessages messages;
	private final List<Smoothie> recipes;
	private final List<String> allIngredients;
	private final List<StateListener> listeners = new CopyOnWriteArrayList<>();

	private AppState state;

	public AppStore(SmoothieService smoothieService, SmoothieRepository repository, UiMessages messages) {
		this.smoothieService = smoothieService;
		this.messages = messages;
		this.recipes = repository.getSmoothies();
		this.allIngredients = smoothieService.getAllIngredients(recipes);
		this.state = buildState(Set.of());
	}

	public AppState getState() {
		return state;
	}

	public void subscribe(StateListener listener) {
		listeners.add(listener);
		notifyListener(listener, state);
	}

	public void dispatch(AppMessage message) {
		if (message instanceof AppMessage.IngredientsSelectionChanged changed) {
			applyState(buildState(changed.selected()));
		} else if (message instanceof AppMessage.SelectAllIngredients) {
			applyState(buildState(Set.copyOf(allIngredients)));
		} else if (message instanceof AppMessage.ClearAllIngredients) {
			applyState(buildState(Set.of()));
		} else if (message instanceof AppMessage.LocaleChanged) {
			applyState(buildState(state.selectedIngredients()));
		} else if (message instanceof AppMessage.LogReportRequested) {
			log.info("Report requested for ingredients {}:\n{}", state.selectedIngredients(),
					smoothieService.formatMakeableReport(recipes, state.selectedIngredients()));
		}
	}

	private void applyState(AppState next) {
		state = next;
		listeners.forEach(listener -> notifyListener(listener, next));
	}

	private void notifyListener(StateListener listener, AppState next) {
		if (SwingUtilities.isEventDispatchThread()) {
			listener.onStateChanged(next);
			return;
		}
		SwingUtilities.invokeLater(() -> listener.onStateChanged(next));
	}

	private AppState buildState(Set<String> selected) {
		List<Smoothie> makeable = smoothieService.findMakeable(recipes, selected);
		List<String> resultLines = makeable.isEmpty()
				? List.of(messages.get("results.empty"))
				: makeable.stream().map(smoothieService::formatListEntry).toList();

		int count = makeable.size();
		String countPart = CountLabels.format(count, messages.get("results.count.noun.singular"),
				messages.get("results.count.noun.plural"));
		String countLabel = messages.get("results.count.available", countPart);
		String selectedSummary = messages.get("ingredients.selected", formatSelectedSummary(selected));

		log.debug("State updated - selected: {}, makeable: {}", selected, count);

		return new AppState(recipes, allIngredients, Set.copyOf(selected), resultLines, countLabel, selectedSummary);
	}

	private String formatSelectedSummary(Set<String> selected) {
		if (selected.isEmpty()) {
			return messages.get("ingredients.selected.none");
		}
		return selected.stream().sorted().collect(Collectors.joining(", "));
	}
}
