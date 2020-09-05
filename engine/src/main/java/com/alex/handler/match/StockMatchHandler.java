package com.alex.handler.match;

import com.alex.bean.command.CmdResultCode;
import com.alex.bean.command.RbCmd;
import com.alex.bean.orderbook.IOrderBook;
import com.alex.handler.BaseHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.util.HashMap;

@RequiredArgsConstructor
public class StockMatchHandler extends BaseHandler {

    @NonNull
    private final HashMap<String, IOrderBook> orderBookMap;

    @Override
    public void onEvent(RbCmd cmd, long sequence, boolean endOfBatch) throws Exception {
        if (cmd.resultCode.getCode() < 0) {
            return;//风控未通过
        }

        cmd.resultCode = processCmd(cmd);

    }

    private CmdResultCode processCmd(RbCmd cmd) {
        switch (cmd.command) {
            case NEW_ORDER:
                return orderBookMap.get(cmd.code).newOrder(cmd);
            case CANCEL_ORDER:
                return orderBookMap.get(cmd.code).cancelOrder(cmd);
            case HQ_PUB:
                orderBookMap.forEach((code, orderBook) ->
                        cmd.marketDataMap.put(code, orderBook.getL1MarketDataSnapshot())
                );
            default:
                return CmdResultCode.SUCCESS;
        }
    }
}
