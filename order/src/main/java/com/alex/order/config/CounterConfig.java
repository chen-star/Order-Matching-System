package com.alex.order.config;

import com.alex.order.bean.MqttBusConsumer;
import io.vertx.core.Vertx;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thirdparty.checksum.ICheckSum;
import thirdparty.codec.IBodyCodec;
import thirdparty.codec.IMsgCodec;

import javax.annotation.PostConstruct;

@Getter
@Component
@Log4j2
public class CounterConfig {

    @Value("${counter.id}")
    private short id;

    @Value("${counter.dataCenterId}")
    private long dataCenterId;

    @Value("${counter.workerId}")
    private long workerId;

    ///////////////////// websocket ////////////////////////////////
    @Value("${counter.pubport}")
    private int pubPort;

    /////////////////// Bus //////////////////////

    @Value("${counter.subbusip}")
    private String subBusIp;

    @Value("${counter.subbusport}")
    private int subBusPort;

    ///////////////////// Gateway ////////////////////////////////

    @Value("${counter.sendip}")
    private String sendIp;

    @Value("${counter.sendport}")
    private int sendPort;

    @Value("${counter.gatewayid}")
    private short gatewayId;

    private Vertx vertx = Vertx.vertx();

    ///////////////////// Encode ////////////////////////////////

    @Value("${counter.checksum}")
    private String checkSumClass;

    @Value("${counter.bodycodec}")
    private String bodyCodecClass;

    @Value("${counter.msgcodec}")
    private String msgCodecClass;


    private ICheckSum cs;

    private IBodyCodec bodyCodec;

    private IMsgCodec msgCodec;

    @PostConstruct
    private void init() {
        Class<?> clz;


        try {
            clz = Class.forName(checkSumClass);
//            clz.newInstance();
            cs = (ICheckSum) clz.getDeclaredConstructor().newInstance();

            clz = Class.forName(bodyCodecClass);
            bodyCodec = (IBodyCodec) clz.getDeclaredConstructor().newInstance();

            clz = Class.forName(msgCodecClass);
            msgCodec = (IMsgCodec) clz.getDeclaredConstructor().newInstance();

        } catch (Exception e) {
            log.error("init config error ", e);
        }


        new MqttBusConsumer(subBusIp, subBusPort, String.valueOf(id),  msgCodec, cs, vertx).startup();
    }
}
