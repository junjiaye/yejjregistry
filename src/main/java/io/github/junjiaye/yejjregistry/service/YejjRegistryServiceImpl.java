package io.github.junjiaye.yejjregistry.service;

import io.github.junjiaye.yejjregistry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @program: yejjregistry
 * @ClassName: YeJjRegistryServiceImpl
 * @description:默认实现
 * @author: yejj
 * @create: 2024-04-15 16:14
 */
@Slf4j
public class YejjRegistryServiceImpl implements RegistryService {

    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();
    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();
    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();
    final static AtomicLong VERSION = new AtomicLong(0);


    final
    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> metas = REGISTRY.get(service);
        if (metas != null && !metas.isEmpty()) {
            if (metas.contains(instance)) {
                log.info("======> instance {} already registered", instance.toUrl());
                instance.setStatus(true);
                return instance;
            }
        }
        log.info(" ======> instance registered {}", instance.toUrl());

        REGISTRY.add(service, instance);
        instance.setStatus(true);
        VERSION.incrementAndGet();
        renew(instance,service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public InstanceMeta unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> matas = REGISTRY.get(service);
        if (matas == null || matas.isEmpty()) {
            return null;
        }
        log.info("======> instance unregistered", instance.toUrl());
        matas.removeIf(l -> l.equals(instance));
        renew(instance,service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public List<InstanceMeta> getAllInstance(String service) {
        return REGISTRY.get(service);
    }

    @Override
    //刷新版本+时间戳
    public long renew(InstanceMeta instance,String... services) {
        long now = System.currentTimeMillis();
        for (String service : services){
            TIMESTAMPS.put(service + "@" + instance.toUrl(), now);
        }
        //VERSIONS.put(service, VERSION.incrementAndGet());
        return now;
    }

    @Override
    public Long version(String service) {
        return VERSIONS.get(service);
    }

    @Override
    public Map<String, Long> versions(String... service) {
        return Arrays.stream(service).collect(Collectors.toMap(x -> x, VERSIONS::get, (x, y) -> x));
    }


}
