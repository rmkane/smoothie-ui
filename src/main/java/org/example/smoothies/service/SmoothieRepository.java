package org.example.smoothies.service;

import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmoothieRepository {

    private final List<Smoothie> smoothies;

    public SmoothieRepository(YamlLoader yamlLoader, SmoothieProperties properties) {
        SmoothiesWrapper wrapper = yamlLoader.load(properties.dataFile(), SmoothiesWrapper.class);
        this.smoothies = List.copyOf(wrapper.smoothies());
    }

    public List<Smoothie> getSmoothies() {
        return smoothies;
    }
}
