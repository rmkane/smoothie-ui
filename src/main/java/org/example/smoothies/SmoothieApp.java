package org.example.smoothies;

import lombok.extern.slf4j.Slf4j;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.service.SmoothieRepository;
import org.example.smoothies.service.SmoothieService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@Slf4j
@SpringBootApplication
public class SmoothieApp extends JFrame {

    private static final String[] INGREDIENTS_LIST = {
        "apple", "butter", "cactus", "egg", "fang", "grapes",
        "guts", "horse", "kelp", "mango", "milk", "nectar",
        "pepper", "potato", "pumpkin", "salt"
    };
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 400;
    private static final int GRID_ROWS = 8;
    private static final int GRID_COLUMNS = 2;
    private static final int UPDATE_INTERVAL_MS = 500;

    private final SmoothieService smoothieService;
    private final List<Smoothie> smoothies;
    private final ConfigurableApplicationContext applicationContext;
    private final List<JCheckBox> ingredientCheckBoxes = new ArrayList<>();
    private final JTextField resultField = new JTextField("hai :D");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SmoothieApp(
            SmoothieService smoothieService,
            SmoothieRepository repository,
            ConfigurableApplicationContext applicationContext) {
        this.smoothieService = smoothieService;
        this.smoothies = repository.getSmoothies();
        this.applicationContext = applicationContext;
        initUI();
        startSmoothieUpdater();
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
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(createIngredientPanel(), BorderLayout.WEST);
        add(resultField, BorderLayout.CENTER);
        add(createCalculateButton(), BorderLayout.EAST);

        resultField.setEditable(false);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
            }
        });
    }

    private JPanel createIngredientPanel() {
        JPanel panel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLUMNS));
        for (String ingredient : INGREDIENTS_LIST) {
            JCheckBox checkBox = new JCheckBox(ingredient);
            ingredientCheckBoxes.add(checkBox);
            panel.add(checkBox);
        }
        return panel;
    }

    private JButton createCalculateButton() {
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> displayPossibleSmoothies());
        return calculateButton;
    }

    private void displayPossibleSmoothies() {
        System.out.println(smoothieService.formatMakeableReport(smoothies, getSelectedIngredients()));
    }

    private Set<String> getSelectedIngredients() {
        return ingredientCheckBoxes.stream()
            .filter(JCheckBox::isSelected)
            .map(JCheckBox::getText)
            .collect(Collectors.toSet());
    }

    private void startSmoothieUpdater() {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Set<String> selected = getSelectedIngredients();
                    int smoothieCount = smoothieService.countMakeable(smoothies, selected);
                    SwingUtilities.invokeLater(() -> resultField.setText(String.valueOf(smoothieCount)));
                    Thread.sleep(UPDATE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private void shutdown() {
        executor.shutdownNow();
        applicationContext.close();
    }
}
