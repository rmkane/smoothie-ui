package org.example.smoothies.ui.component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.springframework.stereotype.Component;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;
import org.example.smoothies.ui.store.AppStore;

@Component
public class IngredientPanel extends JPanel {

	private static final int INGREDIENT_COLUMNS = 2;
	private static final int PANEL_WIDTH = 280;
	private static final int SELECTED_SUMMARY_WIDTH_PX = 250;
	private static final int SELECTED_SUMMARY_MAX_HEIGHT = 72;

	private final AppStore store;
	private final UiMessages messages;
	private final List<JCheckBox> checkBoxes = new ArrayList<>();
	private final JLabel helpLabel;
	private final JLabel selectedLabel;
	private final JButton selectAllButton;
	private final JButton clearAllButton;
	private boolean suppressEvents;

	public IngredientPanel(AppStore store, UiMessages messages) {
		this.store = store;
		this.messages = messages;
		setLayout(new BorderLayout(0, 8));
		setPreferredSize(new Dimension(PANEL_WIDTH, 0));

		AppState initial = store.getState();
		setBorder(
				BorderFactory.createTitledBorder(messages.get("ingredients.border", initial.allIngredients().size())));

		helpLabel = createHelp();
		add(helpLabel, BorderLayout.NORTH);
		add(createCheckboxScroll(initial.allIngredients()), BorderLayout.CENTER);

		selectAllButton = new JButton(messages.get("ingredients.selectAll"));
		selectAllButton.addActionListener(e -> store.dispatch(new AppMessage.SelectAllIngredients()));
		clearAllButton = new JButton(messages.get("ingredients.clearAll"));
		clearAllButton.addActionListener(e -> store.dispatch(new AppMessage.ClearAllIngredients()));
		selectedLabel = new JLabel();
		add(createFooter(), BorderLayout.SOUTH);

		store.subscribe(this::render);
	}

	public void applyLocale() {
		AppState state = store.getState();
		TitledBorder border = BorderFactory
				.createTitledBorder(messages.get("ingredients.border", state.allIngredients().size()));
		setBorder(border);
		helpLabel.setText(messages.get("ingredients.help"));
		selectAllButton.setText(messages.get("ingredients.selectAll"));
		clearAllButton.setText(messages.get("ingredients.clearAll"));
		render(state);
	}

	private JLabel createHelp() {
		JLabel help = new JLabel(messages.get("ingredients.help"));
		help.setBorder(new EmptyBorder(0, 4, 0, 4));
		return help;
	}

	private JScrollPane createCheckboxScroll(List<String> ingredients) {
		int rows = (ingredients.size() + INGREDIENT_COLUMNS - 1) / INGREDIENT_COLUMNS;
		JPanel grid = new JPanel(new GridLayout(rows, INGREDIENT_COLUMNS, 4, 2));
		grid.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));

		for (String ingredient : ingredients) {
			JCheckBox checkBox = new JCheckBox(ingredient);
			checkBox.addActionListener(e -> publishSelection());
			checkBoxes.add(checkBox);
			grid.add(checkBox);
		}

		JScrollPane scroll = new JScrollPane(grid);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		return scroll;
	}

	private JPanel createFooter() {
		JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
		actions.add(selectAllButton);
		actions.add(clearAllButton);

		selectedLabel.setVerticalAlignment(SwingConstants.TOP);
		JScrollPane selectedScroll = new JScrollPane(selectedLabel);
		selectedScroll.setBorder(null);
		selectedScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		selectedScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		selectedScroll.setPreferredSize(new Dimension(0, SELECTED_SUMMARY_MAX_HEIGHT));
		selectedScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, SELECTED_SUMMARY_MAX_HEIGHT));

		JPanel footer = new JPanel(new BorderLayout(0, 6));
		footer.setBorder(new EmptyBorder(0, 4, 0, 4));
		footer.add(actions, BorderLayout.NORTH);
		footer.add(selectedScroll, BorderLayout.CENTER);
		return footer;
	}

	private void publishSelection() {
		if (suppressEvents) {
			return;
		}
		store.dispatch(new AppMessage.IngredientsSelectionChanged(readSelection()));
	}

	private Set<String> readSelection() {
		return checkBoxes.stream().filter(JCheckBox::isSelected).map(JCheckBox::getText).collect(Collectors.toSet());
	}

	private void render(AppState state) {
		suppressEvents = true;
		for (JCheckBox checkBox : checkBoxes) {
			checkBox.setSelected(state.selectedIngredients().contains(checkBox.getText()));
		}
		selectedLabel.setText(formatSelectedSummary(state));
		revalidate();
		repaint();
		suppressEvents = false;
	}

	private String formatSelectedSummary(AppState state) {
		String summary = state.selectedSummary();
		if (state.selectedIngredients().isEmpty()) {
			return summary;
		}
		return "<html><body style='width: %dpx'>%s</body></html>".formatted(SELECTED_SUMMARY_WIDTH_PX, summary);
	}
}
