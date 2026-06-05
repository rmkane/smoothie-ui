package org.example.smoothies.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UiMessages {

	private final MessageSource messageSource;
	private final LocaleHolder localeHolder;

	public String get(String code) {
		return get(code, new Object[0]);
	}

	public String get(String code, Object... args) {
		return messageSource.getMessage(code, args, localeHolder.get());
	}

	public char mnemonic(String code) {
		String value = get(code);
		if (value.isEmpty()) {
			throw new NoSuchMessageException(code, localeHolder.get());
		}
		return value.charAt(0);
	}

	public int mnemonicIndex(String code) {
		return Integer.parseInt(get(code));
	}
}
