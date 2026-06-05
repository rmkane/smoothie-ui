package org.example.smoothies.i18n;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.ui.component.ActionsPanel;
import org.example.smoothies.ui.component.IngredientPanel;
import org.example.smoothies.ui.component.ResultsPanel;
import org.example.smoothies.ui.frame.FrameChrome;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.store.AppStore;

@Component
@RequiredArgsConstructor
public class UiLocaleCoordinator {

	private final LocaleHolder localeHolder;
	private final AppPreferencesStore preferencesStore;
	private final FrameChrome frameChrome;
	private final AppStore appStore;
	private final IngredientPanel ingredientPanel;
	private final ActionsPanel actionsPanel;
	private final ResultsPanel resultsPanel;

	public void refresh(JFrame frame) {
		localeHolder.setFromTag(preferencesStore.get().localeTag());
		frameChrome.install(frame);
		ingredientPanel.applyLocale();
		actionsPanel.applyLocale();
		resultsPanel.applyLocale();
		appStore.dispatch(new AppMessage.LocaleChanged());
	}
}
