package com.alex.handler.pub;


import com.alex.bean.EngineConfig;
import com.alex.bean.command.RbCmd;
import com.alex.bean.orderbook.MatchEvent;
import com.alex.handler.BaseHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.collections.api.tuple.primitive.ShortObjectPair;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ShortObjectHashMap;
import thirdparty.bean.CommonMsg;
import thirdparty.hq.L1MarketData;
import thirdparty.hq.MatchData;
import thirdparty.order.CmdType;

import java.util.List;

import static thirdparty.bean.MsgConstants.*;

@Log4j2
@RequiredArgsConstructor
public class L1PubHandler extends BaseHandler {

    public static final int HQ_PUB_RATE = 5000;


    @NonNull
    private final ShortObjectHashMap<List<MatchData>> matcherEventMap;

    @NonNull
    private EngineConfig config;


    @Override
    public void onEvent(RbCmd cmd, long sequence, boolean endOfBatch) throws Exception {
        final CmdType cmdType = cmd.command;

        if (cmdType == CmdType.NEW_ORDER || cmdType == CmdType.CANCEL_ORDER) {
            for (MatchEvent e : cmd.matchEventList) {
                matcherEventMap.get(e.mid).add(e.copy());
            }
        } else if (cmdType == CmdType.HQ_PUB) {
            //1.五档行情
            pubMarketData(cmd.marketDataMap);
            //2.给柜台发送MatchData
            pubMatcherData();
        }

    }

    private void pubMatcherData() {
        if (matcherEventMap.size() == 0) {
            return;
        }

        log.info(matcherEventMap);

        try {
            for (ShortObjectPair<List<MatchData>> s : matcherEventMap.keyValuesView()) {
                if (CollectionUtils.isEmpty(s.getTwo())) {
                    continue;
                }
                byte[] serialize = config.getBodyCodec().serialize(s.getTwo().toArray(new MatchData[0]));
                pubData(serialize, s.getOne(), MATCH_ORDER_DATA);

                //清空已发送数据
                s.getTwo().clear();

            }
        } catch (Exception e) {
            log.error(e);
        }

    }

    public static final short HQ_ADDRESS = -1;

    private void pubMarketData(IntObjectHashMap<L1MarketData> marketDataMap) {
        log.info(marketDataMap);
        byte[] serialize = null;
        try {
            serialize = config.getBodyCodec().serialize(marketDataMap.values().toArray(new L1MarketData[0]));
        } catch (Exception e) {
            log.error(e);
        }

        if (serialize == null) {
            return;
        }

        pubData(serialize, HQ_ADDRESS, MATCH_HQ_DATA);

    }

    private void pubData(byte[] serialize, short dst, short msgType) {
        CommonMsg msg = new CommonMsg();
        msg.setBodyLength(serialize.length);
        msg.setChecksum(config.getCs().getChecksum(serialize));
        msg.setMsgSrc(config.getId());
        msg.setMsgDst(dst);
        msg.setMsgType(msgType);
        msg.setStatus(NORMAL);
        msg.setBody(serialize);
        config.getBusSender().publish(msg);
    }
}