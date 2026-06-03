package org.example.smoothies.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "smoothies")
public record SmoothieProperties(String dataFile) {
}
