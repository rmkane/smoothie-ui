package org.example.smoothies.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.experimental.UtilityClass;

/** Shared JSON mapper configuration for file I/O and Spring injection. */
@UtilityClass
public class JsonMappers {

	public static ObjectMapper create() {
		return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	}
}
