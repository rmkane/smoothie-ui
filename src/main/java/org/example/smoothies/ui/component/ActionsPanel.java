package org.example.smoothies.ui.component;

import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.message.AppMessage;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class ActionsPanel extends JPanel {

    public ActionsPanel(AppStore store) {
        setLayout(new BorderLayout());

        JButton logReportButton = new JButton("Log full report");
        logReportButton.addActionListener(e -> store.dispatch(new AppMessage.LogReportRequested()));
        add(logReportButton, BorderLayout.EAST);
    }
}
