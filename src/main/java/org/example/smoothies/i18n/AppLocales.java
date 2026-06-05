package org.example.smoothies.i18n;

import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppLocales {

	public static final String SYSTEM_TAG = "";
	public static final String ENGLISH_TAG = "en";
	public static final String SPANISH_TAG = "es";

	public static Locale resolve(String localeTag) {
		if (localeTag == null || localeTag.isBlank()) {
			return Locale.getDefault();
		}
		return Locale.forLanguageTag(localeTag);
	}
}
