package io.github.junjiaye.yejjregistry;

import io.github.junjiaye.yejjregistry.config.YeJJRegistryConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(YeJJRegistryConfigProperties.class)
public class YejjregistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(YejjregistryApplication.class, args);
    }

}
