package com.alex;

import com.alex.bean.QueueConfig;
import thirdparty.codec.BodyCodec;

public class QueueStartup3 {

    public static void main(String[] args) throws Exception {
        String configName = "queue3.properties";
        new QueueConfig(configName, new BodyCodec()).startup();
    }
}
