package org.example.smoothies.service;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

import org.example.smoothies.config.JsonMappers;
import org.example.smoothies.i18n.TestI18n;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.example.smoothies.service.impl.SmoothieServiceImpl;

class SmoothieServiceTest {

	private SmoothieService smoothieService;
	private List<Smoothie> smoothies;

	@BeforeEach
	void setUp() {
		smoothieService = new SmoothieServiceImpl(TestI18n.englishMessages());
		SmoothiesWrapper wrapper = new YamlLoader(new DefaultResourceLoader(), JsonMappers.createYaml())
				.load("data/smoothies.yml", SmoothiesWrapper.class);
		smoothies = wrapper.smoothies();
	}

	@Test
	void getAllIngredientsReturnsSortedUniqueNamesFromRecipes() {
		List<String> ingredients = smoothieService.getAllIngredients(smoothies);

		assertThat(ingredients).isNotEmpty().doesNotHaveDuplicates().isSorted();
		assertThat(ingredients).contains("nectar", "milk", "grapes");
	}

	@Test
	void findMakeableReturnsResultsSortedByName() {
		List<Smoothie> makeable = smoothieService.findMakeable(smoothies, Set.of("nectar", "milk"));

		assertThat(makeable).isNotEmpty();
		assertThat(makeable).extracting(Smoothie::name).isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
	}
}
