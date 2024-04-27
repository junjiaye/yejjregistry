package io.github.junjiaye.yejjregistry.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @program: yejjregistry
 * @ClassName: YeJJRegistryConfigProperties
 * @description:
 * @author: yejj
 * @create: 2024-04-16 20:24
 */
@Data
@ConfigurationProperties(prefix = "yejjregistry")
public class YeJJRegistryConfigProperties {
    private List<String> serverList;
}
