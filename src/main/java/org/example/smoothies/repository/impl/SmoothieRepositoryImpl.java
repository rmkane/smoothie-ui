package org.example.smoothies.repository.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import org.example.smoothies.config.SmoothieProperties;
import org.example.smoothies.io.YamlLoader;
import org.example.smoothies.model.Smoothie;
import org.example.smoothies.model.SmoothiesWrapper;
import org.example.smoothies.repository.SmoothieRepository;

@Slf4j
@Service
public class SmoothieRepositoryImpl implements SmoothieRepository {

	private final List<Smoothie> smoothies;

	public SmoothieRepositoryImpl(YamlLoader yamlLoader, SmoothieProperties properties) {
		this.smoothies = List.copyOf(loadSmoothies(yamlLoader, properties));
	}

	private static List<Smoothie> loadSmoothies(YamlLoader yamlLoader, SmoothieProperties properties) {
		String dataFile = properties.dataFile();
		log.debug("Loading smoothies from classpath resource: {}", dataFile);
		SmoothiesWrapper wrapper = yamlLoader.load(dataFile, SmoothiesWrapper.class);
		log.info("Loaded {} smoothie recipes from {}", wrapper.smoothies().size(), dataFile);
		return wrapper.smoothies();
	}

	@Override
	public List<Smoothie> getSmoothies() {
		return smoothies;
	}
}
