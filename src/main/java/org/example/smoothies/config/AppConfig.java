package org.example.smoothies.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Bean
	Path preferencesFile() {
		return AppDirectories.preferencesFile();
	}
}
