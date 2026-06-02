package org.example.smoothies;

import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.ui.SmoothieFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
@EnableConfigurationProperties(SmoothieProperties.class)
public class SmoothieApp {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(SmoothieApp.class)
            .headless(false)
            .run(args);

        SwingUtilities.invokeLater(() -> {
            SmoothieFrame frame = context.getBean(SmoothieFrame.class);
            frame.setVisible(true);
        });
    }
}
