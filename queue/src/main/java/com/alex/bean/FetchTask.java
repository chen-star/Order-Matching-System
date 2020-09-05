package com.alex.bean;

import com.alipay.sofa.jraft.util.Bits;
import com.alipay.sofa.jraft.util.BytesUtil;
import com.google.common.collect.Lists;
import io.vertx.core.buffer.Buffer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import thirdparty.bean.CmdPack;
import thirdparty.fetchsurv.IFetchService;
import thirdparty.order.OrderCmd;
import thirdparty.order.OrderDirection;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

@RequiredArgsConstructor
public class FetchTask extends TimerTask {

    @NonNull
    private QueueConfig config;

    @Override
    public void run() {
        //遍历网关
        if (!config.getNode().isLeader()) {
            return;
        }

        Map<String, IFetchService> fetchServiceMap = config.getFetchServiceMap();
        if (fetchServiceMap.isEmpty()) {
            return;
        }

        //获取数据
        List<OrderCmd> cmds = collectAllOrders(fetchServiceMap);
        if (cmds.isEmpty()) {
            return;
        }


        //对数据进行排序
        cmds.sort((o1, o2) -> {
            //时间优先 价格优先 量优先
            int res = compareTime(o1, o2);
            if (res != 0) {
                return res;
            }

            res = comparePrice(o1, o2);
            if (res != 0) {
                return res;
            }

            res = compareVolume(o1, o2);
            return res;
        });

        //存储到KV Store，发送到撮合核心

        try {
            // 1. generate package number
            long packetNo = getPacketNoFromStore();

            // 2. store package number
            CmdPack pack = new CmdPack(packetNo, cmds);
            insertToKvStore(packetNo, config.getCodec().serialize(pack));


            // 3. increment package number
            updatePacketNoInStore(packetNo);

            // 4. send package
            config.getMulticastSender().send(
                    Buffer.buffer(),
                    config.getMulticastPort(),
                    config.getMulticastIp(),
                    null
            );
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void updatePacketNoInStore(long packetNo) {
        byte[] val = new byte[8];
        Bits.putLong(val, 0, packetNo);
        config.getNode().getRheaKVStore().put(PACKET_NO_KEY, val);
    }

    private void insertToKvStore(long packetNo, byte[] serialize) {
        byte[] key = new byte[8];
        Bits.putLong(key, 0, packetNo);
        config.getNode().getRheaKVStore().bPut(key, serialize);
    }

    private static final byte[] PACKET_NO_KEY = BytesUtil.writeUtf8("seq_packet_no");

    private long getPacketNoFromStore() {
        final byte[] bPacketNo = config.getNode().getRheaKVStore().bGet(PACKET_NO_KEY);
        long packetNo = 0;
        if (ArrayUtils.isNotEmpty(bPacketNo)) {
            packetNo = Bits.getLong(bPacketNo, 0);
        }
        return packetNo;
    }


    private int compareVolume(OrderCmd o1, OrderCmd o2) {
        if (o1.volume > o2.volume) {
            return -1;
        } else if (o1.volume < o2.volume) {
            return 1;
        } else {
            return 0;
        }
    }

    private int comparePrice(OrderCmd o1, OrderCmd o2) {
        if (o1.direction == o2.direction) {
            if (o1.price > o2.price) {
                return o1.direction == OrderDirection.BUY ? -1 : 1;
            } else if (o1.price < o2.price) {
                return o1.direction == OrderDirection.BUY ? 1 : -1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    private int compareTime(OrderCmd o1, OrderCmd o2) {
        if (o1.timestamp > o2.timestamp) {
            return 1;
        } else if (o1.timestamp < o2.timestamp) {
            return -1;
        } else {
            return 0;
        }
    }

    private List<OrderCmd> collectAllOrders(Map<String, IFetchService> fetchServiceMap) {
        List<OrderCmd> msgs = Lists.newArrayList();
        fetchServiceMap.values().forEach(t -> {
            List<OrderCmd> orderCmds = t.fetchData();
            if (orderCmds != null && !orderCmds.isEmpty()) {
                msgs.addAll(orderCmds);
                System.out.println(msgs);
            }
        });
        return msgs;
    }
}
