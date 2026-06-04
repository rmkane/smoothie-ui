package org.example.smoothies.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class AppConfig {

	@Bean
	Path preferencesFile() {
		return AppDirectories.preferencesFile();
	}

	@Bean
	AppPreferencesStore appPreferencesStore(Path preferencesFile, ObjectMapper objectMapper) {
		return new AppPreferencesStore(preferencesFile, objectMapper);
	}
}
