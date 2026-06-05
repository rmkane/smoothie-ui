package org.example.smoothies.i18n;

import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class LocaleHolder {

	private Locale locale = Locale.getDefault();

	public Locale get() {
		return locale;
	}

	public void set(Locale locale) {
		this.locale = locale != null ? locale : Locale.getDefault();
	}

	public void setFromTag(String localeTag) {
		set(AppLocales.resolve(localeTag));
	}
}
