package io.github.junjiaye.yejjregistry.model;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yejjregistry
 * @ClassName: InstanceMata
 * @description:
 * @author: yejj
 * @create: 2024-04-15 16:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"scheme","host","port","context"})
public class InstanceMeta {

    private String scheme;
    private String host;
    private Integer port;
    private String context;
    private boolean status;
    private Map<String,String> parameters = new HashMap<>();

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.scheme = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http",host,port,"yejjrpc");
    }

    public static InstanceMeta from(String url) {
        URI uri = URI.create(url);
        return new InstanceMeta(uri.getScheme(),uri.getHost(),uri.getPort(),uri.getPath().substring(1));
    }

    public InstanceMeta addParams(Map<String, String> params) {
        this.getParameters().putAll(params);
        return this;
    }
    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
    public String toPath() {
        return String.format("%s_%d",host,port);
    }

    public String toMeta(){
        return JSON.toJSONString(this.getParameters());
    }

}
