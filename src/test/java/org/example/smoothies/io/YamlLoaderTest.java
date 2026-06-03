package org.example.smoothies.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;

class YamlLoaderTest {

	private YamlLoader yamlLoader;

	@BeforeEach
	void setUp() {
		yamlLoader = new YamlLoader();
	}

	@Test
	void loadsSmoothiesYamlFromClasspath() {
		SmoothiesWrapper wrapper = yamlLoader.load("data/smoothies.yml", SmoothiesWrapper.class);

		assertThat(wrapper.smoothies()).isNotEmpty();

		Smoothie first = wrapper.smoothies().get(0);
		assertThat(first.name()).isEqualTo("Nectar Smoothie");
		assertThat(first.ingredients().required()).containsExactly("nectar");
		assertThat(first.ingredients().optional()).containsExactly("nectar");
	}

	@Test
	void throwsWhenResourceIsMissing() {
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> yamlLoader.load("does-not-exist.yaml", SmoothiesWrapper.class));

		assertThat(ex).hasMessage("Classpath resource not found: does-not-exist.yaml");
	}

	@Test
	void throwsWhenYamlIsEmpty() {
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> yamlLoader.load("test-fixtures/empty.yaml", SmoothiesWrapper.class));

		assertThat(ex).hasMessage("YAML was empty: test-fixtures/empty.yaml");
	}

	@Test
	void throwsWhenYamlIsInvalid() {
		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> yamlLoader.load("test-fixtures/invalid.yaml", SmoothiesWrapper.class));

		assertThat(ex).hasMessage("Failed to parse YAML: test-fixtures/invalid.yaml");
		assertThat(ex.getCause()).isNotNull();
	}
}
