package org.example.smoothies.ui.component;

import org.example.smoothies.ui.AppStore;
import org.example.smoothies.ui.message.AppMessage;
import org.example.smoothies.ui.state.AppState;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IngredientPanel extends JPanel {

    private static final int INGREDIENT_COLUMNS = 2;
    private static final int PANEL_WIDTH = 280;
    private static final int SELECTED_SUMMARY_WIDTH_PX = 250;
    private static final int SELECTED_SUMMARY_MAX_HEIGHT = 72;

    private final AppStore store;
    private final List<JCheckBox> checkBoxes = new ArrayList<>();
    private final JLabel selectedLabel = new JLabel();
    private boolean suppressEvents;

    public IngredientPanel(AppStore store) {
        this.store = store;
        setLayout(new BorderLayout(0, 8));
        setPreferredSize(new Dimension(PANEL_WIDTH, 0));

        AppState initial = store.getState();
        setBorder(BorderFactory.createTitledBorder(
            "Ingredients (" + initial.allIngredients().size() + ")"));

        add(createHelp(), BorderLayout.NORTH);
        add(createCheckboxScroll(initial.allIngredients()), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        store.subscribe(this::render);
    }

    private JLabel createHelp() {
        JLabel help = new JLabel(
            "<html>Check each ingredient you have.<br>"
                + "A recipe matches when you have at least one of its "
                + "<b>required</b> and one of its <b>optional</b> items.</html>");
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
        JButton selectAll = new JButton("Select all");
        selectAll.addActionListener(e -> store.dispatch(new AppMessage.SelectAllIngredients()));

        JButton clearAll = new JButton("Clear all");
        clearAll.addActionListener(e -> store.dispatch(new AppMessage.ClearAllIngredients()));

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.add(selectAll);
        actions.add(clearAll);

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
        return checkBoxes.stream()
            .filter(JCheckBox::isSelected)
            .map(JCheckBox::getText)
            .collect(Collectors.toSet());
    }

    private void render(AppState state) {
        suppressEvents = true;
        for (JCheckBox checkBox : checkBoxes) {
            checkBox.setSelected(state.selectedIngredients().contains(checkBox.getText()));
        }
        selectedLabel.setText(formatSelectedSummary(state.selectedSummary()));
        revalidate();
        repaint();
        suppressEvents = false;
    }

    private String formatSelectedSummary(String summary) {
        if ("Selected: (none)".equals(summary)) {
            return summary;
        }
        return "<html><body style='width: " + SELECTED_SUMMARY_WIDTH_PX + "px'>"
            + summary
            + "</body></html>";
    }
}
