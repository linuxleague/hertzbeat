package org.dromara.hertzbeat.collector.dispatch.entrance.processor;

import io.netty.channel.ChannelHandlerContext;
import org.dromara.hertzbeat.collector.dispatch.CommonDispatcher;
import org.dromara.hertzbeat.common.entity.message.ClusterMsg;
import org.dromara.hertzbeat.common.support.SpringContextHolder;
import org.dromara.hertzbeat.remoting.netty.NettyRemotingProcessor;

/**
 * handle collector offline message
 * 注: 这里不关闭与Manager的连接, 只是关闭采集功能
 */
public class GoOfflineProcessor implements NettyRemotingProcessor {
    @Override
    public ClusterMsg.Message handle(ChannelHandlerContext ctx, ClusterMsg.Message message) {
        CommonDispatcher commonDispatcher = SpringContextHolder.getBean(CommonDispatcher.class);
        commonDispatcher.shutdown();
        return null;
    }
}