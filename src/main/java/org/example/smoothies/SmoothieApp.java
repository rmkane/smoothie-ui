package org.example.smoothies;

import lombok.extern.slf4j.Slf4j;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Ingredients;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SmoothieApp {
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

    private final List<JCheckBox> ingredientCheckBoxes = new ArrayList<>();
    private final JTextField resultField = new JTextField("hai :D");
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public SmoothieApp(List<Smoothie> smoothies) {
        JFrame frame = initializeUI(smoothies);
        startSmoothieUpdater(smoothies);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SmoothiesWrapper wrapper = YamlLoader.load("smoothies.yaml", SmoothiesWrapper.class);
        SwingUtilities.invokeLater(() -> new SmoothieApp(wrapper.getSmoothies()));
    }

    private JFrame initializeUI(List<Smoothie> smoothies) {
        JFrame frame = new JFrame("Smoothie Maker");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.add(createIngredientPanel(), BorderLayout.WEST);
        frame.add(resultField, BorderLayout.CENTER);
        frame.add(createCalculateButton(smoothies), BorderLayout.EAST);

        resultField.setEditable(false);

        return frame;
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

    private JButton createCalculateButton(List<Smoothie> smoothies) {
        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> displayPossibleSmoothies(smoothies));
        return calculateButton;
    }

    private void displayPossibleSmoothies(List<Smoothie> smoothies) {
        System.out.println("\n=== SMOOTHIES YOU CAN MAKE ===");
        boolean smoothiesFound = false;

        for (Smoothie smoothie : smoothies) {
            Ingredients ingredients = smoothie.getIngredients();
            if (canMakeSmoothie(ingredients)) {
                smoothiesFound = true;
                System.out.printf("Smoothie: %s%n", smoothie.getName());
                System.out.printf("  - Required: %s%n", String.join(", ", ingredients.getRequired()));
                System.out.printf("  - Optional: %s%n%n", String.join(", ", ingredients.getOptional()));
            }
        }

        if (!smoothiesFound) {
            System.out.println("No smoothies can be made with the selected ingredients.");
        }

        System.out.println("================================");
    }

    private boolean canMakeSmoothie(Ingredients ingredients) {
        return ingredients.getRequired().stream().anyMatch(this::isIngredientSelected) &&
            ingredients.getOptional().stream().anyMatch(this::isIngredientSelected);
    }

    private boolean isIngredientSelected(String ingredient) {
        return ingredientCheckBoxes.stream()
            .anyMatch(box -> box.getText().equals(ingredient) && box.isSelected());
    }

    private void startSmoothieUpdater(List<Smoothie> smoothies) {
        executor.submit(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int smoothieCount = calculateSmoothiesCount(smoothies);
                    SwingUtilities.invokeLater(() -> resultField.setText(String.valueOf(smoothieCount)));
                    Thread.sleep(UPDATE_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private int calculateSmoothiesCount(List<Smoothie> smoothies) {
        int count = 0;
        for (Smoothie smoothie : smoothies) {
            Ingredients ingredients = smoothie.getIngredients();
            if (canMakeSmoothie(ingredients)) {
                count++;
            }
        }
        return count;
    }
}
