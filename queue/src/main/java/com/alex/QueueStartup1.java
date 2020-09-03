package com.alex;

import com.alex.bean.QueueConfig;

public class QueueStartup1 {

    public static void main(String[] args) throws Exception {
        String configName = "queue1.properties";
        new QueueConfig(configName).startup();
    }
}
