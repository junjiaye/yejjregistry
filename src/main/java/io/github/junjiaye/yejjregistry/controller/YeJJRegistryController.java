package io.github.junjiaye.yejjregistry.controller;

import io.github.junjiaye.yejjregistry.model.InstanceMeta;
import io.github.junjiaye.yejjregistry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @program: yejjregistry
 * @ClassName: YeJJRegistryController
 * @description:
 * @author: yejj
 * @create: 2024-04-15 16:44
 */
@RestController
@Slf4j
public class YeJJRegistryController {
    @Autowired
    RegistryService registryService;

    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("register {} $$ {}",service, instance);
        return registryService.register(service, instance);
    }
    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("unregister {} $$ {}",service, instance);
        return registryService.unregister(service, instance);
    }
    @RequestMapping("/findAll")
    public List<InstanceMeta> findAll(@RequestParam String service){
        log.info("get {} ",service);
        return registryService.getAllInstance(service);
    }
    @RequestMapping("/renew")
    public long renew(@RequestBody InstanceMeta instance,@RequestParam String... services){
        log.info("renew {} $$ {}",services, instance);
        return registryService.renew(instance,services);
    }
    @RequestMapping("/version")
    public Long version(@RequestParam String service){
        log.info("version {}",service);
        return registryService.version(service);
    }
    @RequestMapping("/versions")
    public Map<String, Long> versions(@RequestParam String... service){
        log.info("versions {}",service);
        return registryService.versions(service);
    }
}
