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
                throw new RuntimeException(resourcePath + " not found");
            }
            return yaml.load(inputStream);
        } catch (IOException e) {
            log.error("Could not load: {}", resourcePath);
            throw new RuntimeException(e);
        }
    }
}
