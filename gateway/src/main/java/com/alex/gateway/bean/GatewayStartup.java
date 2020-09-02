package com.alex.gateway.bean;

import thirdparty.checksum.ByteCheckSum;
import thirdparty.codec.BodyCodec;

public class GatewayStartup {

    public static void main(String[] args) throws Exception {
        String configFileName = "gateway.xml";

        GatewayConfig config = new GatewayConfig();
        config.initConfig(GatewayStartup.class.getResource("/").getPath()
                + configFileName);
        config.setCs(new ByteCheckSum());
        config.setBodyCodec(new BodyCodec());
        config.startup();
    }
}
