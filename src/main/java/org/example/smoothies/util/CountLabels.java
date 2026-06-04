package org.example.smoothies.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CountLabels {

	public static String format(int count, String singular, String plural) {
		String noun = count == 1 ? singular : plural;
		return "%d %s".formatted(count, noun);
	}
}
