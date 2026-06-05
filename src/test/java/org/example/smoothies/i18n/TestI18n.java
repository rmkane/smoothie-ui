package org.example.smoothies.i18n;

import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestI18n {

	public static UiMessages englishMessages() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("messages");
		source.setDefaultEncoding("UTF-8");
		LocaleHolder holder = new LocaleHolder();
		holder.set(Locale.ENGLISH);
		return new UiMessages(source, holder);
	}
}
