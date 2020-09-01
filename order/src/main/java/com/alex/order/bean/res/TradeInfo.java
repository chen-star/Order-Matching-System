package com.alex.order.bean.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TradeInfo {

    private int id;
    private long uid;
    private String code;
    private String name;
    private int direction;
    private float price;
    private int tcount;
    private int oid;
    private String date;
    private String time;

}
