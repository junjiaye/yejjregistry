package io.github.junjiaye.yejjregistry.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @program: yejjregistry
 * @ClassName: Server
 * @description:
 * @author: yejj
 * @create: 2024-04-16 20:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"url"})
public class Server {
    private String url;
    private boolean status;
    private boolean leader;
    private Long version;
}
