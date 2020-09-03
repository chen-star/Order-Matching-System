package com.alex;

import com.alex.bean.QueueConfig;

public class QueueStartup2 {

    public static void main(String[] args) throws Exception {
        String configName = "queue2.properties";
        new QueueConfig(configName).startup();
    }
}
