package org.example.smoothies;

import javax.swing.SwingUtilities;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

import org.example.smoothies.config.AppDirectories;
import org.example.smoothies.config.AppPreferences;
import org.example.smoothies.config.AppPreferencesStore;
import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.ui.frame.SmoothieFrame;
import org.example.smoothies.ui.theme.LookAndFeelSupport;

@SpringBootApplication
@EnableConfigurationProperties(SmoothieProperties.class)
public class SmoothieApp {

	public static void main(String[] args) {
		AppDirectories.configureLoggingDirectory();
		AppPreferences bootstrap = AppPreferencesStore.bootstrap();
		LookAndFeelSupport.apply(bootstrap.theme(), bootstrap.uiScale());

		ConfigurableApplicationContext context = new SpringApplicationBuilder(SmoothieApp.class).headless(false)
				.run(args);

		SwingUtilities.invokeLater(() -> {
			SmoothieFrame frame = context.getBean(SmoothieFrame.class);
			frame.setVisible(true);
		});
	}
}
