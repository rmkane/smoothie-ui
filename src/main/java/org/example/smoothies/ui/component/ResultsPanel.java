package org.example.smoothies.ui.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.springframework.stereotype.Component;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.state.AppState;
import org.example.smoothies.ui.store.AppStore;

@Component
public class ResultsPanel extends JPanel {

	private final UiMessages messages;
	private final DefaultListModel<String> listModel = new DefaultListModel<>();
	private final JList<String> resultsList = new JList<>(listModel);
	private final JLabel countLabel = new JLabel(" ");
	private final JScrollPane resultsScroll;

	public ResultsPanel(AppStore store, UiMessages messages) {
		this.messages = messages;
		setLayout(new BorderLayout(0, 8));

		resultsList.setVisibleRowCount(12);
		resultsList.setFixedCellHeight(-1);
		countLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

		add(countLabel, BorderLayout.NORTH);
		resultsScroll = new JScrollPane(resultsList);
		resultsScroll.setBorder(BorderFactory.createTitledBorder(messages.get("results.border")));
		add(resultsScroll, BorderLayout.CENTER);

		store.subscribe(this::render);
	}

	public void applyLocale() {
		resultsScroll.setBorder(BorderFactory.createTitledBorder(messages.get("results.border")));
	}

	private void render(AppState state) {
		countLabel.setText(state.countLabel());
		listModel.clear();
		state.resultLines().forEach(listModel::addElement);
	}
}
