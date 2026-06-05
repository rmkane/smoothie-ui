package org.example.smoothies.i18n;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.assertj.core.api.Assertions.assertThat;

class UiMessagesTest {

	private UiMessages messages;
	private LocaleHolder localeHolder;

	@BeforeEach
	void setUp() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("messages");
		source.setDefaultEncoding("UTF-8");
		localeHolder = new LocaleHolder();
		messages = new UiMessages(source, localeHolder);
	}

	@Test
	void resolvesEnglishMenuLabel() {
		localeHolder.set(Locale.ENGLISH);
		assertThat(messages.get("menu.file")).isEqualTo("File");
	}

	@Test
	void resolvesSpanishMenuLabel() {
		localeHolder.set(Locale.forLanguageTag("es"));
		assertThat(messages.get("menu.file")).isEqualTo("Archivo");
	}
}
