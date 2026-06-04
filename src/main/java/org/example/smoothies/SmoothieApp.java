package org.example.smoothies;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.ui.LookAndFeelSupport;
import org.example.smoothies.ui.SmoothieFrame;

@SpringBootApplication
@EnableConfigurationProperties(SmoothieProperties.class)
public class SmoothieApp {

	public static void main(String[] args) {
		LookAndFeelSupport.apply(AppPreferencesStore.bootstrap().theme());

		ConfigurableApplicationContext context = new SpringApplicationBuilder(SmoothieApp.class).headless(false)
				.run(args);

		SwingUtilities.invokeLater(() -> {
			SmoothieFrame frame = context.getBean(SmoothieFrame.class);
			frame.setVisible(true);
		});
	}
}
