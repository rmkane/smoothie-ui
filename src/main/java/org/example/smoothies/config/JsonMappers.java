package org.example.smoothies.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.experimental.UtilityClass;

/** Shared Jackson mapper configuration for file I/O and Spring injection. */
@UtilityClass
public class JsonMappers {

	public static ObjectMapper createJson() {
		return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
	}

	public static ObjectMapper createYaml() {
		return new ObjectMapper(new YAMLFactory());
	}
}
