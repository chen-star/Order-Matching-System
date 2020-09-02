package com.alex.order.service;

import com.alex.order.bean.res.OrderInfo;
import com.alex.order.bean.res.PosiInfo;
import com.alex.order.bean.res.TradeInfo;

import java.util.List;

public interface OrderService {

    //查资金
    Long getBalance(long uid);

    //查持仓
    List<PosiInfo> getPostList(long uid);

    //查委托
    List<OrderInfo> getOrderList(long uid);

    //查成交
    List<TradeInfo> getTradeList(long uid);

    //发送委托
    boolean sendOrder(long uid, short type, long timestamp,
                      String code, byte direction, long price, long volume,
                      byte ordertype);

}