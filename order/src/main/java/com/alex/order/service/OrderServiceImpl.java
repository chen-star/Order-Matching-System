package com.alex.order.service;

import com.alex.order.bean.res.OrderInfo;
import com.alex.order.bean.res.PosiInfo;
import com.alex.order.bean.res.TradeInfo;
import com.alex.order.util.DbUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public Long getBalance(long uid) {
        return DbUtil.getBalance(uid);
    }

    @Override
    public List<PosiInfo> getPostList(long uid) {
        return DbUtil.getPosiList(uid);
    }

    @Override
    public List<OrderInfo> getOrderList(long uid) {
        return DbUtil.getOrderList(uid);
    }

    @Override
    public List<TradeInfo> getTradeList(long uid) {
        return DbUtil.getTradeList(uid);
    }

//    @Autowired
//    private CounterConfig config;
//
//    @Override
//    public boolean sendOrder(long uid, short type, long timestamp, int code,
//                             byte direction, long price, long volume, byte ordertype) {
//        final OrderCmd orderCmd = OrderCmd.builder()
//                .type(CmdType.of(type))
//                .timestamp(timestamp)
//                .mid(config.getId())
//                .uid(uid)
//                .code(code)
//                .direction(OrderDirection.of(ordertype))
//                .price(price)
//                .volume(volume)
//                .orderType(OrderType.of(ordertype))
//                .build();
//
//        //1.入库
//        int oid = DbUtil.saveOrder(orderCmd);
//        if(oid < 0){
//            return false;
//        }else {
//            //TODO 发送网关
//            log.info(orderCmd);
//            return true;
//        }
//
//
//    }
}