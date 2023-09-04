package org.dromara.hertzbeat.collector.dispatch.entrance.processor;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.dromara.hertzbeat.collector.dispatch.entrance.CollectServer2;
import org.dromara.hertzbeat.common.entity.job.Job;
import org.dromara.hertzbeat.common.entity.message.ClusterMsg;
import org.dromara.hertzbeat.common.util.JsonUtil;
import org.dromara.hertzbeat.remoting.netty.NettyRemotingProcessor;

/**
 * handle cyclic data message
 */
@Slf4j
public class CollectCyclicDataProcessor implements NettyRemotingProcessor {
    private final CollectServer2 collectServer2;

    public CollectCyclicDataProcessor(CollectServer2 collectServer2) {
        this.collectServer2 = collectServer2;
    }

    @Override
    public ClusterMsg.Message handle(ChannelHandlerContext ctx, ClusterMsg.Message message) {
        Job job = JsonUtil.fromJson(message.getMsg(), Job.class);
        if (job == null) {
            log.error("collector receive cyclic task job is null");
            return null;
        }
        collectServer2.getCollectJobService().addAsyncCollectJob(job);
        return null;
    }
}
