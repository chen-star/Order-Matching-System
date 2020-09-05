package thirdparty.bus;

import com.google.common.primitives.Shorts;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import thirdparty.bean.CommonMsg;
import thirdparty.codec.IMsgCodec;

import java.util.concurrent.TimeUnit;

@Log4j2
@RequiredArgsConstructor
public class MqttBusSender implements IBusSender{

    @NonNull
    private String ip;

    @NonNull
    private int port;

    @NonNull
    private IMsgCodec msgCodec;

    @NonNull
    private Vertx vertx;

    private volatile  MqttClient sender;

    @Override
    public void startup() {
        mqttConnect();
    }

    private void mqttConnect() {
        MqttClient mqttClient = MqttClient.create(vertx);
        mqttClient.connect(port, ip, res -> {
            if (res.succeeded()) {
                log.info("connected to mqtt bus {}:{}", ip, port);
                sender = mqttClient;
            } else {
                log.error("connect to {}:{} failed", ip, port);
                mqttConnect();
            }
        });

        mqttClient.closeHandler(h -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    @Override
    public void publish(CommonMsg commonMsg) {
        sender.publish(Short.toString(commonMsg.getMsgDst()),
                msgCodec.encodeToBuffer(commonMsg),
                MqttQoS.AT_LEAST_ONCE,
                false,
                false);
    }
}
