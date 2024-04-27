package io.github.junjiaye.yejjregistry.cluster;

import io.github.junjiaye.yejjregistry.config.YeJJRegistryConfigProperties;
import io.github.junjiaye.yejjregistry.http.HttpInvoker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @program: yejjregistry
 * @ClassName: Cluster
 * @description:
 * @author: yejj
 * @create: 2024-04-16 20:20
 */
@Slf4j
@Data
public class Cluster {

    @Value("${server.port}")
    String port;

    String host;

    Server MYSELF;

    YeJJRegistryConfigProperties properties;

    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public Cluster(YeJJRegistryConfigProperties properties) {
        this.properties = properties;
    }

    List<Server> servers;

    public void init() {
        host = new InetUtils(new InetUtilsProperties()).findFirstNonLoopbackHostInfo().getIpAddress();
        MYSELF = new Server("http://" + host + ":" + port, true, false, -1L);
        System.out.println("=======>MYSELE=" + MYSELF);
        List<Server> servers = new ArrayList<>();
        properties.getServerList().forEach(url -> {
            Server server = new Server();
            if(url.contains("localhost")){
                url = url.replace("localhost",host);
            }else if (url.contains("127.0.0.1")){
                url = url.replace("127.0.0.1",host);
            }
            if (url.equals(MYSELF.getUrl())){
                servers.add(MYSELF);
            }else {
                server.setUrl(url);
                server.setStatus(false);
                server.setLeader(false);
                server.setVersion(-1L); //初始版本为-1 表示没有初始化
                servers.add(server);
            }
        });
        this.servers = servers;
        //探活，健康检查
        executor.scheduleAtFixedRate(() -> {
            updateServer();
            electLeader();
        }, 0, 10000, TimeUnit.MILLISECONDS);
    }

    private void electLeader() {
        List<Server> leaders = this.servers.stream().filter(server -> server.isStatus()).filter(Server::isLeader).collect(Collectors.toList());
        if (leaders.isEmpty()) {
            //没有leader,则选一个主
            System.out.println("=======>elect for no leader:" + servers);
            elect();
        } else if (leaders.size() > 1) {
            //有多个leader，也需要重新选主
            System.out.println("=======>elect for more than one leader:" + servers);
            elect();
        }else {
            System.out.println("=======>no need election for leader:" + leaders.get(0));
        }
    }

    private void elect() {
        //三种选主方式
        //1.各个节点自己选，算法保证大家选的是同一个
        //2.外部有一个分布式锁，谁抢到锁，谁就是leader
        //3.分布式一致性算法，比如paxos,raft
        Server candidate = null; //候选者
        for (Server server : servers) {
            //多个主的情况下，需要先清空主节点，防止脑裂
            server.setLeader(false);
            if (server.isStatus()) {
                if (candidate == null) {
                    candidate = server;
                } else {
                    if (candidate.hashCode() > server.hashCode()) {
                        candidate = server;
                    }
                }
            }

        }


        if (candidate != null) {
            candidate.setLeader(true);
            System.out.println("========>>> elect for leader" + candidate);
        }

    }

    private void updateServer() {
        //parallel : 并行处理
        servers.stream().parallel().forEach(server -> {
            try {
                if (server.equals(MYSELF)){
                    return;
                }
                Server serverInfo = HttpInvoker.httpGet(server.getUrl()+"/info", Server.class);
                if (serverInfo != null) {
                    server.setVersion(serverInfo.getVersion());
                    server.setStatus(true);
                    server.setLeader(serverInfo.isLeader());
                }
            } catch (Exception e) {
                System.out.println("=======>health check failed for " + server);
                server.setStatus(false);
                server.setLeader(false);
            }
        });
    }

    public Server self() {
        return MYSELF;
    }

    public Server leader() {
        return this.servers.stream().filter(server -> server.isStatus()).filter(Server::isLeader).findFirst().orElse(null);
    }


}
