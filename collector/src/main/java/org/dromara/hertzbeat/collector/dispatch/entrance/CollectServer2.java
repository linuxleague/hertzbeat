package org.dromara.hertzbeat.collector.dispatch.entrance;


import org.dromara.hertzbeat.collector.dispatch.DispatchProperties;
import org.dromara.hertzbeat.collector.dispatch.entrance.internal.CollectJobService;
import org.dromara.hertzbeat.collector.dispatch.entrance.processor.CollectCyclicDataProcessor;
import org.dromara.hertzbeat.collector.dispatch.entrance.processor.CollectOneTimeDataProcessor;
import org.dromara.hertzbeat.collector.dispatch.entrance.processor.GoOfflineProcessor;
import org.dromara.hertzbeat.collector.dispatch.entrance.processor.GoOnlineProcessor;
import org.dromara.hertzbeat.collector.dispatch.entrance.processor.HeartbeatProcessor;
import org.dromara.hertzbeat.common.entity.message.ClusterMsg;
import org.dromara.hertzbeat.common.support.CommonThreadPool;
import org.dromara.hertzbeat.remoting.RemotingClient;
import org.dromara.hertzbeat.remoting.netty.NettyClientConfig;
import org.dromara.hertzbeat.remoting.netty.NettyRemotingClient;

/**
 * collect server
 */
public class CollectServer2 {

    private final CollectJobService collectJobService;

    private RemotingClient remotingClient;

    public CollectServer2(final CollectJobService collectJobService,
                          final DispatchProperties properties,
                          final CommonThreadPool threadPool) {
        this.collectJobService = collectJobService;

        this.init(properties, threadPool);
        this.remotingClient.start();
    }

    private void init(final DispatchProperties properties, final CommonThreadPool threadPool) {
        NettyClientConfig nettyClientConfig = new NettyClientConfig();
        DispatchProperties.EntranceProperties.NettyProperties nettyProperties = properties.getEntrance().getNetty();
        nettyClientConfig.setServerIp(nettyProperties.getManagerIp());
        nettyClientConfig.setServerPort(nettyProperties.getManagerPort());
        this.remotingClient = new NettyRemotingClient(nettyClientConfig, null, threadPool);

        this.remotingClient.registerProcessor(ClusterMsg.MessageType.HEARTBEAT, new HeartbeatProcessor());
        this.remotingClient.registerProcessor(ClusterMsg.MessageType.ISSUE_CYCLIC_TASK, new CollectCyclicDataProcessor(this));
        this.remotingClient.registerProcessor(ClusterMsg.MessageType.ISSUE_ONE_TIME_TASK, new CollectOneTimeDataProcessor(this));
        this.remotingClient.registerProcessor(ClusterMsg.MessageType.GO_OFFLINE, new GoOfflineProcessor());
        this.remotingClient.registerProcessor(ClusterMsg.MessageType.GO_ONLINE, new GoOnlineProcessor());
    }

    public void shutdown() {
        this.remotingClient.shutdown();
    }

    public CollectJobService getCollectJobService() {
        return collectJobService;
    }
}
