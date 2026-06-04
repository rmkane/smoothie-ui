package org.example.smoothies.io;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class YamlLoader {

	private final ResourceLoader resourceLoader;
	private final ObjectMapper yamlMapper;

	public YamlLoader(ResourceLoader resourceLoader, @Qualifier("yamlObjectMapper") ObjectMapper yamlMapper) {
		this.resourceLoader = resourceLoader;
		this.yamlMapper = yamlMapper;
	}

	public <E> E load(String resourcePath, Class<E> clazz) {
		Resource resource = resourceLoader.getResource("classpath:" + resourcePath);
		if (!resource.exists()) {
			throw new IllegalStateException("Classpath resource not found: " + resourcePath);
		}
		try (InputStream inputStream = resource.getInputStream()) {
			E result = yamlMapper.readValue(inputStream, clazz);
			if (result == null) {
				throw new IllegalStateException("YAML was empty: " + resourcePath);
			}
			log.debug("Loaded {} from classpath", resourcePath);
			return result;
		} catch (MismatchedInputException e) {
			if (isEmptyInput(e)) {
				throw new IllegalStateException("YAML was empty: " + resourcePath, e);
			}
			log.error("Failed to parse YAML: {}", resourcePath, e);
			throw new IllegalStateException("Failed to parse YAML: " + resourcePath, e);
		} catch (JsonProcessingException e) {
			log.error("Failed to parse YAML: {}", resourcePath, e);
			throw new IllegalStateException("Failed to parse YAML: " + resourcePath, e);
		} catch (IOException e) {
			log.error("Failed to read YAML: {}", resourcePath, e);
			throw new IllegalStateException("Failed to read YAML: " + resourcePath, e);
		}
	}

	private static boolean isEmptyInput(MismatchedInputException e) {
		String message = e.getOriginalMessage();
		return message != null && message.contains("No content to map due to end-of-input");
	}
}
