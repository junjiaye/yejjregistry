package io.github.junjiaye.yejjregistry.config;

import io.github.junjiaye.yejjregistry.health.YeJJHealthChecker;
import io.github.junjiaye.yejjregistry.service.RegistryService;
import io.github.junjiaye.yejjregistry.service.YejjRegistryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: yejjregistry
 * @ClassName: YeJJRegistryConfig
 * @description:
 * @author: yejj
 * @create: 2024-04-15 16:45
 */
@Configuration
public class YeJJRegistryConfig {
    @Bean
    public RegistryService registryService() {
        return new YejjRegistryServiceImpl();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public YeJJHealthChecker healthChecker(@Autowired RegistryService registryService) {
        return new YeJJHealthChecker(registryService);
    }
}
