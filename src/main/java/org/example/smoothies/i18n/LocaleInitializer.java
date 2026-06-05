package org.example.smoothies.i18n;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.config.AppPreferencesStore;

@Component
@RequiredArgsConstructor
public class LocaleInitializer {

	private final AppPreferencesStore preferencesStore;
	private final LocaleHolder localeHolder;

	@PostConstruct
	void applySavedLocale() {
		localeHolder.setFromTag(preferencesStore.get().localeTag());
	}
}
