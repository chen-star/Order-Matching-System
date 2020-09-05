package com.alex;

import com.alex.bean.QueueConfig;
import thirdparty.codec.BodyCodec;

public class QueueStartup2 {

    public static void main(String[] args) throws Exception {
        String configName = "queue2.properties";
        new QueueConfig(configName, new BodyCodec()).startup();
    }
}
