package org.example.smoothies.io;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class YamlLoader {

    private static final ClassLoader classLoader = YamlLoader.class.getClassLoader();

    public static <E> E load(String resourcePath, Class<E> clazz) {
        Yaml yaml = new Yaml(new Constructor(clazz, new LoaderOptions()));
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Classpath resource not found: " + resourcePath);
            }
            E result;
            try {
                result = yaml.load(inputStream);
            } catch (RuntimeException e) {
                log.error("Failed to parse YAML: {}", resourcePath, e);
                throw new IllegalStateException("Failed to parse YAML: " + resourcePath, e);
            }
            if (result == null) {
                throw new IllegalStateException("YAML was empty: " + resourcePath);
            }
            return result;
        } catch (IOException e) {
            log.error("Failed to read YAML: {}", resourcePath, e);
            throw new IllegalStateException("Failed to read YAML: " + resourcePath, e);
        }
    }
}
