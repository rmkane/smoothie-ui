package org.example.smoothies.service;

import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SmoothieServiceTest {

    private SmoothieService smoothieService;
    private List<Smoothie> smoothies;

    @BeforeEach
    void setUp() {
        smoothieService = new SmoothieService();
        SmoothiesWrapper wrapper = new YamlLoader().load("data/smoothies.yml", SmoothiesWrapper.class);
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
        assertThat(makeable)
            .extracting(Smoothie::name)
            .isSortedAccordingTo(String.CASE_INSENSITIVE_ORDER);
    }
}
