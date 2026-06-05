package org.example.smoothies.ui.component;

import java.awt.*;

import javax.swing.*;

import org.springframework.stereotype.Component;

import org.example.smoothies.i18n.UiMessages;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.store.AppStore;

@Component
public class ActionsPanel extends JPanel {

	private final UiMessages messages;
	private final JButton logReportButton;

	public ActionsPanel(AppStore store, UiMessages messages) {
		this.messages = messages;
		setLayout(new BorderLayout());

		logReportButton = new JButton(messages.get("actions.logReport"));
		logReportButton.addActionListener(e -> store.dispatch(new AppMessage.LogReportRequested()));
		add(logReportButton, BorderLayout.EAST);
	}

	public void applyLocale() {
		logReportButton.setText(messages.get("actions.logReport"));
	}
}
