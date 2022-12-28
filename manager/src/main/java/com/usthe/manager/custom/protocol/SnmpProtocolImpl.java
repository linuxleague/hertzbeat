package com.usthe.manager.custom.protocol;

import com.usthe.common.annotation.ProtocolType;
import com.usthe.common.entity.job.Metrics;
import com.usthe.common.entity.job.protocol.SnmpProtocol;
import com.usthe.common.util.JacksonUtil;
import com.usthe.manager.custom.ProtocolTypeDiptach;

import java.util.Map;

/**
 * Created by za-jiangliwen on 2022/9/9
 *
 * @author za-jiangliwen
 * @date 2022/9/9
 */
@ProtocolType(name = "snmp")
public class SnmpProtocolImpl implements ProtocolTypeDiptach {
    @Override
    public Map<String, Object> dispatch(Metrics request) {
        JacksonUtil<SnmpProtocol> jacksonUtil = new JacksonUtil<>();
        return jacksonUtil.readValue(request.getSnmp());
    }
}