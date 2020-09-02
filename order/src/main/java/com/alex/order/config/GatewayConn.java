package com.alex.order.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import thirdparty.bean.CommonMsg;
import thirdparty.order.OrderCmd;
import thirdparty.tcp.TcpDirectSender;
import thirdparty.uuid.UuidGenerator;

import javax.annotation.PostConstruct;

import static thirdparty.bean.MsgConstants.COUNTER_NEW_ORDER;
import static thirdparty.bean.MsgConstants.NORMAL;

@Configuration
@Log4j2
public class GatewayConn {

    @Autowired
    private CounterConfig config;

    private TcpDirectSender directSender;

    @PostConstruct
    private void init() {
        directSender = new TcpDirectSender(config.getSendIp(), config.getSendPort(), config.getVertx());
        directSender.startup();
    }

    public void sendOrder(OrderCmd orderCmd) {
        byte[] data = null;
        try {
            data = config.getBodyCodec().serialize(orderCmd);
        } catch (Exception e) {
            log.error("encode error for ordercmd:{}", orderCmd, e);
            return;
        }

        CommonMsg msg = new CommonMsg();
        System.out.println(msg);
        msg.setBodyLength(data.length);
        msg.setChecksum(config.getCs().getChecksum(data));
        msg.setMsgSrc(config.getId());
        msg.setMsgDst(config.getGatewayId());
        msg.setMsgType(COUNTER_NEW_ORDER);
        msg.setStatus(NORMAL);
        msg.setMsgNo(UuidGenerator.getInstance().getUUID());
        msg.setBody(data);
        directSender.send(config.getMsgCodec().encodeToBuffer(msg));
    }

}