package com.alex.bean.orderbook;



import lombok.NoArgsConstructor;
import lombok.ToString;
import thirdparty.hq.MatchData;
import thirdparty.order.OrderStatus;

//不能命名为TradeEvnet ，因为除了成交事件 还有撤单等其他event
@NoArgsConstructor
@ToString
public final class MatchEvent {

    public long timestamp;

    public short mid;

    public long oid;

    public OrderStatus status = OrderStatus.NOT_SET;

    public long tid;

    //撤单数量 成交数量
    public long volume;

    public long price;


    public MatchData copy() {
        return MatchData.builder()
                .timestamp(this.timestamp)
                .mid(this.mid)
                .oid(this.oid)
                .status(this.status)
                .tid(this.tid)
                .volume(this.volume)
                .price(this.price)
                .build();

    }



}