package org.example.smoothies.io;

import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class YamlLoaderTest {

    @Test
    void loadsSmoothiesYamlFromClasspath() {
        SmoothiesWrapper wrapper = YamlLoader.load("smoothies.yaml", SmoothiesWrapper.class);

        assertThat(wrapper.getSmoothies()).isNotEmpty();

        Smoothie first = wrapper.getSmoothies().get(0);
        assertThat(first.getName()).isEqualTo("Nectar Smoothie");
        assertThat(first.getIngredients().getRequired()).containsExactly("nectar");
        assertThat(first.getIngredients().getOptional()).containsExactly("nectar");
    }

    @Test
    void throwsWhenResourceIsMissing() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> YamlLoader.load("does-not-exist.yaml", SmoothiesWrapper.class));

        assertThat(ex).hasMessage("Classpath resource not found: does-not-exist.yaml");
    }

    @Test
    void throwsWhenYamlIsEmpty() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> YamlLoader.load("test-fixtures/empty.yaml", SmoothiesWrapper.class));

        assertThat(ex).hasMessage("YAML was empty: test-fixtures/empty.yaml");
    }

    @Test
    void throwsWhenYamlIsInvalid() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> YamlLoader.load("test-fixtures/invalid.yaml", SmoothiesWrapper.class));

        assertThat(ex).hasMessage("Failed to parse YAML: test-fixtures/invalid.yaml");
        assertThat(ex.getCause()).isNotNull();
    }
}
