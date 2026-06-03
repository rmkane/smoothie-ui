package org.example.smoothies.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.ui.component.ActionsPanel;
import org.example.smoothies.ui.component.IngredientPanel;
import org.example.smoothies.ui.component.ResultsPanel;
import org.example.smoothies.ui.state.AppState;

@Slf4j
@Component
public class SmoothieFrame extends JFrame {

	private static final int WINDOW_WIDTH = 780;
	private static final int WINDOW_HEIGHT = 520;

	private final ConfigurableApplicationContext applicationContext;

	public SmoothieFrame(AppStore store, IngredientPanel ingredientPanel, ResultsPanel resultsPanel,
			ActionsPanel actionsPanel, ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;

		setTitle("Smoothie Maker");
		AppIcons.applyTo(this);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setLayout(new BorderLayout(12, 12));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

		add(ingredientPanel, BorderLayout.WEST);
		add(resultsPanel, BorderLayout.CENTER);
		add(actionsPanel, BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shutdown();
			}
		});

		AppState state = store.getState();
		log.info("Smoothie Maker ready — {} recipes, {} ingredients", state.recipes().size(),
				state.allIngredients().size());
	}

	private void shutdown() {
		log.info("Shutting down Smoothie Maker");
		applicationContext.close();
	}
}
