package com.alex.bean;

import com.alex.core.EngineApi;
import com.alipay.remoting.exception.CodecException;
import com.alipay.sofa.jraft.rhea.client.RheaKVStore;
import com.alipay.sofa.jraft.rhea.storage.KVEntry;
import com.alipay.sofa.jraft.util.Bits;
import com.google.common.collect.Lists;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import thirdparty.bean.CmdPack;
import thirdparty.codec.IBodyCodec;
import thirdparty.order.OrderCmd;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Log4j2
public class CmdPacketQueue {

    private static CmdPacketQueue ourInstance = new CmdPacketQueue();

    private CmdPacketQueue() {
    }

    public static CmdPacketQueue getInstance() {
        return ourInstance;
    }

    ////////////////////////////////////////////////////////////////////////

    private final BlockingQueue<CmdPack> recvCache = new LinkedBlockingDeque<>();

    public void cache(CmdPack pack) {
        recvCache.offer(pack);
    }

    ////////////////////////////////////////////////////////////////////////

    private RheaKVStore orderKVStore;

    private IBodyCodec codec;

    private EngineApi engineApi;


    public void init(RheaKVStore orderKVStore, IBodyCodec codec, EngineApi engineApi) {
        this.orderKVStore = orderKVStore;
        this.codec = codec;
        this.engineApi = engineApi;

        new Thread(() -> {
            while (true) {
                try {
                    CmdPack cmds = recvCache.poll(10, TimeUnit.SECONDS);
                    if (cmds != null) {
                        handle(cmds);
                    }
                } catch (Exception e) {
                    log.error("msg packet recvcache error,continue", e);
                }
            }
        }).start();
    }

    private long lastPackNo = -1;

    private void handle(CmdPack cmdPack) throws Exception {
        log.info("recv : {}", cmdPack);

        //NACK
        long packNo = cmdPack.getPackNo();
        if (packNo == lastPackNo + 1) {
            if (CollectionUtils.isEmpty(cmdPack.getOrderCmds())) {
                return;
            }
            for (OrderCmd cmd : cmdPack.getOrderCmds()) {
                engineApi.submitCommand(cmd);
            }
        } else if (packNo <= lastPackNo) {
            //来自历史的重复的包
            log.warn("recv duplicate packId : {}", packNo);
        } else {
            //跳号
            log.info("packNo lost from {} to {},begin query from sequencer", lastPackNo + 1, packNo);
            //请求数据
            byte[] firstKey = new byte[8];
            Bits.putLong(firstKey, 0, lastPackNo + 1);

            byte[] lastKey = new byte[8];
            Bits.putLong(lastKey, 0, packNo + 1);

            final List<KVEntry> kvEntries = orderKVStore.bScan(firstKey, lastKey);
            if (CollectionUtils.isNotEmpty(kvEntries)) {
                List<CmdPack> collect = Lists.newArrayList();
                for (KVEntry entry : kvEntries) {
                    byte[] value = entry.getValue();
                    if (ArrayUtils.isNotEmpty(value)) {
                        collect.add(codec.deserialize(value, CmdPack.class));
                    }
                }
                collect.sort((o1, o2) -> (int) (o1.getPackNo() - o2.getPackNo()));
                for (CmdPack pack : collect) {
                    if (CollectionUtils.isEmpty(pack.getOrderCmds())) {
                        continue;
                    }
                    for (OrderCmd cmd : pack.getOrderCmds()) {
                        engineApi.submitCommand(cmd);
                    }
                }
            }
            //排队机出错 导致出现了跳号
            lastPackNo = packNo;

        }

    }


}
