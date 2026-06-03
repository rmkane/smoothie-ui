package org.example.smoothies.model;

import java.util.List;

public record Ingredients(List<String> required, List<String> optional) {
}
