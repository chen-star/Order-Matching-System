package com.alex;

import com.alex.bean.QueueConfig;
import thirdparty.codec.BodyCodec;

public class QueueStartup1 {

    public static void main(String[] args) throws Exception {
        String configName = "queue1.properties";
        new QueueConfig(configName, new BodyCodec()).startup();
    }
}
