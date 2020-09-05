package com.alex.bean;

import com.alex.bean.command.CmdResultCode;
import com.alex.bean.command.RbCmd;
import com.alipay.sofa.jraft.rhea.util.Lists;
import com.lmax.disruptor.EventFactory;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.util.HashMap;

public class RbCmdFactory implements EventFactory<RbCmd> {
    @Override
    public RbCmd newInstance() {
        return RbCmd.builder()
                .resultCode(CmdResultCode.SUCCESS)
                .matchEventList(Lists.newArrayList())
                .marketDataMap(new HashMap<>())
                .build();
    }
}
