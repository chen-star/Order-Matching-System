package com.alex;

import com.alex.bean.QueueConfig;

public class QueueStartup3 {

    public static void main(String[] args) throws Exception {
        String configName = "queue3.properties";
        new QueueConfig(configName).startup();
    }
}
