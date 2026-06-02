package org.example.smoothies;

import lombok.extern.slf4j.Slf4j;
import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.repository.SmoothieRepository;
import org.example.smoothies.service.SmoothieService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(SmoothieProperties.class)
public class SmoothieApp extends JFrame {

    private static final int WINDOW_WIDTH = 780;
    private static final int WINDOW_HEIGHT = 520;
    private static final int INGREDIENT_COLUMNS = 2;
    private static final String EMPTY_RESULTS_MESSAGE =
        "No smoothies match — select required and optional ingredients.";

    private final SmoothieService smoothieService;
    private final List<Smoothie> smoothies;
    private final List<String> ingredients;
    private final ConfigurableApplicationContext applicationContext;
    private final List<JCheckBox> ingredientCheckBoxes = new ArrayList<>();
    private final DefaultListModel<String> resultsModel = new DefaultListModel<>();
    private final JList<String> resultsList = new JList<>(resultsModel);
    private final JLabel countLabel = new JLabel(" ");
    private final JLabel selectedIngredientsLabel = new JLabel("Selected: (none)");

    public SmoothieApp(
        SmoothieService smoothieService,
        SmoothieRepository repository,
        ConfigurableApplicationContext applicationContext) {
        this.smoothieService = smoothieService;
        this.smoothies = repository.getSmoothies();
        this.ingredients = smoothieService.getAllIngredients(smoothies);
        this.applicationContext = applicationContext;
        initUI();
        refreshResults();
        log.info("Smoothie Maker ready — {} recipes, {} ingredients", smoothies.size(), ingredients.size());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SmoothieApp.class)
            .headless(false)
            .run(args);

        SwingUtilities.invokeLater(() -> {
            SmoothieApp app = context.getBean(SmoothieApp.class);
            app.setVisible(true);
        });
    }

    private void initUI() {
        setTitle("Smoothie Maker");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new BorderLayout(12, 12));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createIngredientPanel(), BorderLayout.WEST);
        add(createResultsPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
    }

    private JPanel createIngredientPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        TitledBorder border = BorderFactory.createTitledBorder(
            "Ingredients (" + ingredients.size() + ")");
        panel.setBorder(border);
        panel.setPreferredSize(new Dimension(260, 0));

        panel.add(createIngredientsHelp(), BorderLayout.NORTH);

        JScrollPane checkboxScroll = new JScrollPane(createIngredientCheckboxes());
        checkboxScroll.setBorder(null);
        checkboxScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        checkboxScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(checkboxScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.add(createIngredientActions());
        footer.add(Box.createVerticalStrut(4));
        selectedIngredientsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(selectedIngredientsLabel);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel createIngredientsHelp() {
        JLabel help = new JLabel(
            "<html>Check each ingredient you have.<br>"
                + "A recipe matches when you have at least one of its "
                + "<b>required</b> and one of its <b>optional</b> items.</html>");
        help.setAlignmentX(Component.LEFT_ALIGNMENT);
        help.setBorder(new EmptyBorder(0, 4, 0, 4));
        return help;
    }

    private JPanel createIngredientCheckboxes() {
        int rows = (ingredients.size() + INGREDIENT_COLUMNS - 1) / INGREDIENT_COLUMNS;
        JPanel checkboxes = new JPanel(new GridLayout(rows, INGREDIENT_COLUMNS, 4, 2));
        checkboxes.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 4));

        for (String ingredient : ingredients) {
            JCheckBox checkBox = new JCheckBox(ingredient);
            checkBox.addActionListener(e -> refreshResults());
            ingredientCheckBoxes.add(checkBox);
            checkboxes.add(checkBox);
        }

        return checkboxes;
    }

    private JPanel createIngredientActions() {
        JButton selectAll = new JButton("Select all");
        selectAll.addActionListener(e -> setAllIngredients(true));

        JButton clearAll = new JButton("Clear all");
        clearAll.addActionListener(e -> setAllIngredients(false));

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(selectAll);
        actions.add(Box.createHorizontalStrut(8));
        actions.add(clearAll);
        return actions;
    }

    private void setAllIngredients(boolean selected) {
        ingredientCheckBoxes.forEach(box -> box.setSelected(selected));
        refreshResults();
    }

    private JPanel createResultsPanel() {
        resultsList.setVisibleRowCount(12);
        resultsList.setFixedCellHeight(-1);

        countLabel.setBorder(new EmptyBorder(0, 0, 6, 0));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(countLabel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Smoothies you can make"));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JButton calculateButton = new JButton("Log full report");
        calculateButton.addActionListener(e -> logMakeableReport());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(calculateButton, BorderLayout.EAST);
        return panel;
    }

    private void refreshResults() {
        Set<String> selected = getSelectedIngredients();
        List<Smoothie> makeable = smoothieService.findMakeable(smoothies, selected);

        updateSelectedIngredientsLabel(selected);

        resultsModel.clear();
        if (makeable.isEmpty()) {
            resultsModel.addElement(EMPTY_RESULTS_MESSAGE);
        } else {
            makeable.stream()
                .map(smoothieService::formatListEntry)
                .forEach(resultsModel::addElement);
        }

        int count = makeable.size();
        countLabel.setText(count == 1 ? "1 smoothie available" : count + " smoothies available");
        log.debug("Ingredients selected: {} — {} makeable smoothie(s)", selected, count);
    }

    private void updateSelectedIngredientsLabel(Set<String> selected) {
        if (selected.isEmpty()) {
            selectedIngredientsLabel.setText("Selected: (none)");
        } else {
            String names = selected.stream().sorted().collect(Collectors.joining(", "));
            selectedIngredientsLabel.setText("Selected: " + names);
        }
    }

    private void logMakeableReport() {
        Set<String> selected = getSelectedIngredients();
        String report = smoothieService.formatMakeableReport(smoothies, selected);
        log.info("Calculate requested for ingredients {}:\n{}", selected, report);
    }

    private Set<String> getSelectedIngredients() {
        return ingredientCheckBoxes.stream()
            .filter(JCheckBox::isSelected)
            .map(JCheckBox::getText)
            .collect(Collectors.toSet());
    }

    private void shutdown() {
        log.info("Shutting down Smoothie Maker");
        applicationContext.close();
    }
}
