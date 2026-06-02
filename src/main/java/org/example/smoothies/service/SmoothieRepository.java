package org.example.smoothies.service;

import lombok.extern.slf4j.Slf4j;
import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SmoothieRepository {

    private final List<Smoothie> smoothies;

    public SmoothieRepository(YamlLoader yamlLoader, SmoothieProperties properties) {
        String dataFile = properties.dataFile();
        log.debug("Loading smoothies from classpath resource: {}", dataFile);
        SmoothiesWrapper wrapper = yamlLoader.load(dataFile, SmoothiesWrapper.class);
        this.smoothies = List.copyOf(wrapper.smoothies());
        log.info("Loaded {} smoothie recipes from {}", smoothies.size(), dataFile);
    }

    public List<Smoothie> getSmoothies() {
        return smoothies;
    }
}
