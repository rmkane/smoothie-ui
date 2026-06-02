package org.example.smoothies.service;

import org.example.smoothies.model.Ingredients;
import org.example.smoothies.model.Smoothie;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SmoothieService {

    public boolean canMake(Ingredients ingredients, Set<String> selected) {
        return ingredients.getRequired().stream().anyMatch(selected::contains)
            && ingredients.getOptional().stream().anyMatch(selected::contains);
    }

    public int countMakeable(List<Smoothie> smoothies, Set<String> selected) {
        return (int) smoothies.stream()
            .filter(s -> canMake(s.getIngredients(), selected))
            .count();
    }

    public List<Smoothie> findMakeable(List<Smoothie> smoothies, Set<String> selected) {
        return smoothies.stream()
            .filter(s -> canMake(s.getIngredients(), selected))
            .toList();
    }

    public String formatMakeableReport(List<Smoothie> smoothies, Set<String> selected) {
        List<Smoothie> makeable = findMakeable(smoothies, selected);
        StringBuilder report = new StringBuilder("\n=== SMOOTHIES YOU CAN MAKE ===\n");

        if (makeable.isEmpty()) {
            report.append("No smoothies can be made with the selected ingredients.\n");
        } else {
            report.append(makeable.stream()
                .map(this::formatSmoothieEntry)
                .collect(Collectors.joining()));
        }

        report.append("================================");
        return report.toString();
    }

    private String formatSmoothieEntry(Smoothie smoothie) {
        Ingredients ingredients = smoothie.getIngredients();
        return String.format(
            "Smoothie: %s%n  - Required: %s%n  - Optional: %s%n%n",
            smoothie.getName(),
            String.join(", ", ingredients.getRequired()),
            String.join(", ", ingredients.getOptional()));
    }
}
