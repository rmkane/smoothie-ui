package org.example.smoothies.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import org.example.smoothies.model.Ingredients;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.service.SmoothieService;

@Service
public class SmoothieServiceImpl implements SmoothieService {

	@Override
	public List<String> getAllIngredients(List<Smoothie> smoothies) {
		return smoothies.stream()
				.flatMap(s -> Stream.concat(s.ingredients().required().stream(), s.ingredients().optional().stream()))
				.distinct().sorted(Comparator.naturalOrder()).toList();
	}

	@Override
	public boolean canMake(Ingredients ingredients, Set<String> selected) {
		return ingredients.required().stream().anyMatch(selected::contains)
				&& ingredients.optional().stream().anyMatch(selected::contains);
	}

	@Override
	public int countMakeable(List<Smoothie> smoothies, Set<String> selected) {
		return (int) smoothies.stream().filter(s -> canMake(s.ingredients(), selected)).count();
	}

	@Override
	public List<Smoothie> findMakeable(List<Smoothie> smoothies, Set<String> selected) {
		return smoothies.stream().filter(s -> canMake(s.ingredients(), selected))
				.sorted(Comparator.comparing(Smoothie::name, String.CASE_INSENSITIVE_ORDER)).toList();
	}

	@Override
	public String formatListEntry(Smoothie smoothie) {
		Ingredients ingredients = smoothie.ingredients();
		return String.format("%s  (required: %s, optional: %s)", smoothie.name(),
				String.join(", ", ingredients.required()), String.join(", ", ingredients.optional()));
	}

	@Override
	public String formatMakeableReport(List<Smoothie> smoothies, Set<String> selected) {
		List<Smoothie> makeable = findMakeable(smoothies, selected);
		StringBuilder report = new StringBuilder("Makeable smoothies: ").append(makeable.size()).append('\n');

		if (makeable.isEmpty()) {
			report.append("  (none — adjust your ingredient selection)");
		} else {
			makeable.forEach(s -> report.append("  • ").append(formatListEntry(s)).append('\n'));
		}
		return report.toString();
	}
}
