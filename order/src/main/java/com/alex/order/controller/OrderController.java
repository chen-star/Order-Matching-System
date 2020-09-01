package com.alex.order.controller;

import com.alex.order.bean.res.CounterRes;
import com.alex.order.bean.res.OrderInfo;
import com.alex.order.bean.res.PosiInfo;
import com.alex.order.bean.res.TradeInfo;
import com.alex.order.service.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
@Log4j2
public class OrderController {

//    @Autowired
//    private StockCache stockCache;

//    @RequestMapping("/code")
//    public CounterRes sotckQuery(@RequestParam String key){
//        Collection<StockInfo> stocks = stockCache.getStocks(key);
//        return new CounterRes(stocks);
//    }


    @Autowired
    private OrderService orderService;


    @RequestMapping("/balance")
    public CounterRes balanceQuery(@RequestParam long uid)
            throws Exception{
        long balance = orderService.getBalance(uid);
        return new CounterRes(balance);
    }

    @RequestMapping("/posiinfo")
    public CounterRes posiQuery(@RequestParam long uid)
            throws Exception{
        log.info(uid);
        List<PosiInfo> postList = orderService.getPostList(uid);
        return new CounterRes(postList);
    }

    @RequestMapping("/orderinfo")
    public CounterRes orderQuery(@RequestParam long uid)
            throws Exception{
        List<OrderInfo> orderList = orderService.getOrderList(uid);
        return new CounterRes(orderList);
    }

    @RequestMapping("/tradeinfo")
    public CounterRes tradeQuery(@RequestParam long uid)
            throws Exception{
        List<TradeInfo> tradeList = orderService.getTradeList(uid);
        return new CounterRes(tradeList);
    }

//    @RequestMapping("/sendorder")
//    public CounterRes order(
//            @RequestParam int uid,
//            @RequestParam short type,
//            @RequestParam long timestamp,
//            @RequestParam int code,
//            @RequestParam byte direction,
//            @RequestParam long price,
//            @RequestParam long volume,
//            @RequestParam byte ordertype
//    ){
//        if(orderService.sendOrder(uid,type,timestamp,code,direction,price,
//                volume,ordertype)){
//            return new CounterRes(SUCCESS,"save success",null);
//        }else {
//            return new CounterRes(FAIL,"save failed",null);
//        }
//
//    }

}