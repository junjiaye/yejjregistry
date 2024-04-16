package io.github.junjiaye.yejjregistry.health;

import io.github.junjiaye.yejjregistry.model.InstanceMeta;
import io.github.junjiaye.yejjregistry.service.RegistryService;
import io.github.junjiaye.yejjregistry.service.YejjRegistryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @program: yejjregistry
 * @ClassName: YeJJHealthChecker
 * @description:
 * @author: yejj
 * @create: 2024-04-16 18:02
 */
@Slf4j
public class YeJJHealthChecker implements HealthChecker {

    RegistryService registryService;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    long TIMEOUT = 20_000;

    public YeJJHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void start() {
        executor.scheduleWithFixedDelay(
                () -> {
                    log.info("health checker running");
                    long now = System.currentTimeMillis();
                    YejjRegistryServiceImpl.TIMESTAMPS.keySet().stream().forEach(serviceAndInst -> {
                        long timestamp = YejjRegistryServiceImpl.TIMESTAMPS.get(serviceAndInst);
                        if (now - timestamp > TIMEOUT) {
                            log.info("serviceAndInst {} is down", serviceAndInst);
                            int i = serviceAndInst.indexOf("@");
                            String url = serviceAndInst.substring(i + 1);
                            InstanceMeta meta = InstanceMeta.from(url);
                            registryService.unregister(meta.getContext(), meta);
                            YejjRegistryServiceImpl.TIMESTAMPS.remove(serviceAndInst);
                        }
                    });
                },
                10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executor.shutdown();
    }
}
