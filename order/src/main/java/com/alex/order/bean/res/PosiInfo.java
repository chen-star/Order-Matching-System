package com.alex.order.bean.res;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class PosiInfo {

    private int id;
    private long uid;
    private String code;
    private String name;
    private long cost;
    private long count;


}