package org.example.smoothies.ui.frame;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.ui.component.ActionsPanel;
import org.example.smoothies.ui.component.IngredientPanel;
import org.example.smoothies.ui.component.ResultsPanel;
import org.example.smoothies.ui.menu.AppMenuBar;
import org.example.smoothies.ui.session.SessionRestore;
import org.example.smoothies.ui.state.AppState;
import org.example.smoothies.ui.store.AppStore;
import org.example.smoothies.ui.support.AppIcons;
import org.example.smoothies.ui.support.AppInfo;

@Slf4j
@Component
public class SmoothieFrame extends JFrame {

	private static final int WINDOW_WIDTH = 780;
	private static final int WINDOW_HEIGHT = 520;

	private final ConfigurableApplicationContext applicationContext;

	public SmoothieFrame(AppStore store, IngredientPanel ingredientPanel, ResultsPanel resultsPanel,
			ActionsPanel actionsPanel, AppMenuBar appMenuBar, AppPreferencesStore preferencesStore,
			SessionRestore sessionRestore, ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		AppIcons.applyTo(this);
		appMenuBar.install(this);

		setTitle(AppInfo.NAME);
		setLayout(new BorderLayout(12, 12));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		add(ingredientPanel, BorderLayout.WEST);
		add(resultsPanel, BorderLayout.CENTER);
		add(actionsPanel, BorderLayout.SOUTH);

		restoreWindowBounds(preferencesStore.get());
		sessionRestore.restoreIngredientSelection(store, preferencesStore.get());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				preferencesStore.saveSession(store.getState().selectedIngredients(), getBounds());
				shutdown();
			}
		});

		AppState state = store.getState();
		log.info("Smoothie Maker ready — {} recipes, {} ingredients", state.recipes().size(),
				state.allIngredients().size());
	}

	private void restoreWindowBounds(AppPreferences preferences) {
		if (preferences.windowBounds() != null) {
			setBounds(preferences.windowBounds().toRectangle());
		} else {
			setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
			setLocationRelativeTo(null);
		}
	}

	private void shutdown() {
		log.info("Shutting down Smoothie Maker");
		applicationContext.close();
	}
}
