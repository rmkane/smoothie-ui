package org.example.smoothies.config;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppDirectoriesTest {

	@Test
	void resolveConfigDirectoryUsesAppId() {
		assertThat(AppDirectories.resolveConfigDirectory()).asString().endsWith(AppDirectories.APP_ID);
	}

	@Test
	void logsDirectoryIsUnderConfigDirectory() {
		assertThat(AppDirectories.logsDirectory().getParent()).isEqualTo(AppDirectories.configDirectory());
		assertThat(AppDirectories.logsDirectory().getFileName()).isEqualTo(Path.of("logs"));
	}
}
