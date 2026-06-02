package org.example.smoothies.io;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class YamlLoader {

    private final ClassLoader classLoader;
    private final ObjectMapper yamlMapper;

    public YamlLoader() {
        this.classLoader = YamlLoader.class.getClassLoader();
        this.yamlMapper = new ObjectMapper(new YAMLFactory());
    }

    public <E> E load(String resourcePath, Class<E> clazz) {
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Classpath resource not found: " + resourcePath);
            }
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
