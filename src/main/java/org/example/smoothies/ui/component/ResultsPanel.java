package org.example.smoothies.ui.component;

import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.state.AppState;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@Component
public class ResultsPanel extends JPanel {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> resultsList = new JList<>(listModel);
    private final JLabel countLabel = new JLabel(" ");

    public ResultsPanel(AppStore store) {
        setLayout(new BorderLayout(0, 8));

        resultsList.setVisibleRowCount(12);
        resultsList.setFixedCellHeight(-1);
        countLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

        add(countLabel, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(resultsList);
        scroll.setBorder(BorderFactory.createTitledBorder("Smoothies you can make"));
        add(scroll, BorderLayout.CENTER);

        store.subscribe(this::render);
    }

    private void render(AppState state) {
        countLabel.setText(state.countLabel());
        listModel.clear();
        state.resultLines().forEach(listModel::addElement);
    }
}
