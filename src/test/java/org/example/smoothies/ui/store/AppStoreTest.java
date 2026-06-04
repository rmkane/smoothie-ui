package org.example.smoothies.ui.store;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.smoothies.config.JsonMappers;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.example.smoothies.repository.SmoothieRepository;
import org.example.smoothies.service.SmoothieService;
import org.example.smoothies.service.impl.SmoothieServiceImpl;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;

class AppStoreTest {

	private AppStore store;
	private List<Smoothie> recipes;

	@BeforeEach
	void setUp() {
		SmoothieService service = new SmoothieServiceImpl();
		SmoothiesWrapper wrapper = new YamlLoader(new DefaultResourceLoader(), JsonMappers.createYaml())
				.load("data/smoothies.yml", SmoothiesWrapper.class);
		recipes = wrapper.smoothies();
		SmoothieRepository repository = () -> recipes;
		store = new AppStore(service, repository);
	}

	@Test
	void initialStateHasNoSelection() {
		AppState state = store.getState();

		assertThat(state.selectedIngredients()).isEmpty();
		assertThat(state.resultLines()).containsExactly(AppState.EMPTY_RESULTS_MESSAGE);
		assertThat(state.countLabel()).isEqualTo("0 smoothies available");
	}

	@Test
	void dispatchUpdatesMakeableSmoothies() {
		store.dispatch(new AppMessage.IngredientsSelectionChanged(Set.of("nectar", "milk")));

		AppState state = store.getState();
		assertThat(state.selectedIngredients()).containsExactlyInAnyOrder("nectar", "milk");
		assertThat(state.resultLines()).isNotEmpty();
		assertThat(state.resultLines()).anyMatch(line -> line.contains("Nectar") || line.contains("Milk"));
	}

	@Test
	void selectAllAndClearAllUpdateState() {
		store.dispatch(new AppMessage.SelectAllIngredients());
		assertThat(store.getState().selectedIngredients())
				.containsExactlyInAnyOrderElementsOf(store.getState().allIngredients());

		store.dispatch(new AppMessage.ClearAllIngredients());
		assertThat(store.getState().selectedIngredients()).isEmpty();
	}

	@Test
	void subscribeReceivesUpdatedState() throws Exception {
		AtomicReference<AppState> latest = new AtomicReference<>();
		store.subscribe(latest::set);

		store.dispatch(new AppMessage.IngredientsSelectionChanged(Set.of("mango")));
		SwingUtilities.invokeAndWait(() -> {
		});

		assertThat(latest.get().selectedIngredients()).containsExactly("mango");
	}
}
