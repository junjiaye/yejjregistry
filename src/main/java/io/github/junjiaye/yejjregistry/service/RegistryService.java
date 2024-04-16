package io.github.junjiaye.yejjregistry.service;

import io.github.junjiaye.yejjregistry.model.InstanceMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: yejjregistry
 * @ClassName: RegistryService
 * @description:interface for registry service.
 * @author: yejj
 * @create: 2024-04-15 15:48
 */
public interface RegistryService {
    //最基础的功能
    InstanceMeta register(String service, InstanceMeta instance);

    InstanceMeta unregister(String service, InstanceMeta instance);

    List<InstanceMeta> getAllInstance(String service);
    //TODO 高级功能

    long renew(InstanceMeta instance,String... service);

    Long version(String service);

    Map<String, Long> versions(String... service);
}
