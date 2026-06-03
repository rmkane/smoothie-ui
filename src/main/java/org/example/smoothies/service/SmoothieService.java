package org.example.smoothies.service;

import java.util.List;
import java.util.Set;

import org.example.smoothies.model.Ingredients;
import org.example.smoothies.model.Smoothie;

public interface SmoothieService {

	List<String> getAllIngredients(List<Smoothie> smoothies);

	boolean canMake(Ingredients ingredients, Set<String> selected);

	int countMakeable(List<Smoothie> smoothies, Set<String> selected);

	List<Smoothie> findMakeable(List<Smoothie> smoothies, Set<String> selected);

	String formatListEntry(Smoothie smoothie);

	String formatMakeableReport(List<Smoothie> smoothies, Set<String> selected);
}
