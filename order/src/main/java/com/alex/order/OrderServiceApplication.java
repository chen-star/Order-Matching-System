package com.alex.order;

import com.alex.order.config.CounterConfig;
import com.alex.order.util.DbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import thirdparty.uuid.UuidGenerator;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class OrderServiceApplication {

    @Autowired
    private DbUtil dbUtil;

    @Autowired
    private CounterConfig counterConfig;

    @PostConstruct
    private void init() {
        UuidGenerator.getInstance().init(counterConfig.getDataCenterId(), counterConfig.getWorkerId());
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
