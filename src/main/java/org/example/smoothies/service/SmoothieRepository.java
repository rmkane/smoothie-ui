package org.example.smoothies.service;

import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmoothieRepository {

    private final List<Smoothie> smoothies;

    public SmoothieRepository() {
        SmoothiesWrapper wrapper = YamlLoader.load("smoothies.yaml", SmoothiesWrapper.class);
        this.smoothies = List.copyOf(wrapper.getSmoothies());
    }

    public List<Smoothie> getSmoothies() {
        return smoothies;
    }
}
