package com.alex.core;

import com.alex.bean.RbCmdFactory;
import com.alex.bean.command.RbCmd;
import com.alex.handler.BaseHandler;
import com.alex.handler.exception.DisruptorExceptionHandler;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import net.openhft.affinity.AffinityStrategies;
import net.openhft.affinity.AffinityThreadFactory;
import thirdparty.order.CmdType;
import thirdparty.order.OrderCmd;

import java.util.Timer;
import java.util.TimerTask;

import static com.alex.handler.pub.L1PubHandler.HQ_PUB_RATE;

@Log4j2
public class EngineCore {

    private final Disruptor<RbCmd> disruptor;

    private static final int RING_BUFFER_SIZE = 1024;

    @Getter
    private final EngineApi api;

    public EngineCore(
            @NonNull final BaseHandler riskHandler,
            @NonNull final BaseHandler matchHandler,
            @NonNull final BaseHandler pubHandler
    ) {
        this.disruptor = new Disruptor<>(
                new RbCmdFactory(),

                RING_BUFFER_SIZE,

                new AffinityThreadFactory("aft_engine_core", AffinityStrategies.ANY),

                ProducerType.SINGLE,

                new BlockingWaitStrategy()
        );

        this.api = new EngineApi(disruptor.getRingBuffer());

        //1.全局异常处理器
        final DisruptorExceptionHandler<RbCmd> exceptionHandler = new DisruptorExceptionHandler<>(
                "main",
                (ex, seq) -> {
                    log.error("exception thrown on seq={}", seq, ex);
                });
        disruptor.setDefaultExceptionHandler(exceptionHandler);

        //2. 前置风控 --> 撮合 --> 发布数据
        disruptor.handleEventsWith(riskHandler)
                .then(matchHandler)
                .then(pubHandler);

        //3.启动
        disruptor.start();
        log.info("match engine start");


        //4.定时发布行情任务
        new Timer().schedule(new HqPubTask(), 5000, HQ_PUB_RATE);

    }


    private class HqPubTask extends TimerTask {

        @Override
        public void run() {
            api.submitCommand(
                    OrderCmd.builder()
                            .type(CmdType.HQ_PUB)
                            .build()
            );
        }
    }


}