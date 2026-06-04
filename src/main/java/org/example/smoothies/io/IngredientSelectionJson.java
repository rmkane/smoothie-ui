package org.example.smoothies.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IngredientSelectionJson {

	private final ObjectMapper mapper;

	public void write(Path path, IngredientSelectionDocument document) throws IOException {
		Files.writeString(path, mapper.writeValueAsString(document));
		log.info("Exported ingredient selection to {}", path);
	}

	public IngredientSelectionDocument read(Path path) throws IOException {
		String json = Files.readString(path);
		try {
			IngredientSelectionDocument document = mapper.readValue(json, IngredientSelectionDocument.class);
			if (document == null) {
				throw new IllegalArgumentException("JSON file is empty");
			}
			if (document.formatVersion() != IngredientSelectionDocument.CURRENT_FORMAT_VERSION) {
				throw new IllegalArgumentException("Unsupported format version: %d (expected %d)"
						.formatted(document.formatVersion(), IngredientSelectionDocument.CURRENT_FORMAT_VERSION));
			}
			if (document.selectedIngredients() == null) {
				throw new IllegalArgumentException("Missing selectedIngredients");
			}
			log.info("Imported ingredient selection from {} ({} items)", path, document.selectedIngredients().size());
			return document;
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Invalid JSON: " + e.getOriginalMessage(), e);
		}
	}
}
