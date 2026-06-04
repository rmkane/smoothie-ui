package org.example.smoothies.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountLabelsTest {

	@Test
	void zeroUsesPluralNoun() {
		assertThat(CountLabels.format(0, "smoothie", "smoothies")).isEqualTo("0 smoothies");
	}

	@Test
	void oneUsesSingularNoun() {
		assertThat(CountLabels.format(1, "smoothie", "smoothies")).isEqualTo("1 smoothie");
	}

	@Test
	void multipleUsesPluralNoun() {
		assertThat(CountLabels.format(2, "smoothie", "smoothies")).isEqualTo("2 smoothies");
	}

	@Test
	void acceptsCustomSingularAndPlural() {
		assertThat(CountLabels.format(3, "recipe", "recipes")).isEqualTo("3 recipes");
	}
}
