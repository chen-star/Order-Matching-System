package com.alex.gateway.bean.handler;

import com.alex.gateway.bean.OrderCmdContainer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thirdparty.bean.CommonMsg;
import thirdparty.codec.IBodyCodec;
import thirdparty.order.OrderCmd;

@AllArgsConstructor
public class MsgHandler implements IMsgHandler {

    private IBodyCodec bodyCodec;

    @Override
    public void onCounterData(CommonMsg msg) {
        OrderCmd orderCmd;

        try {
            orderCmd = bodyCodec.deserialize(msg.getBody(), OrderCmd.class);
            OrderCmdContainer.getInstance().cache(orderCmd);
            System.out.println(orderCmd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
