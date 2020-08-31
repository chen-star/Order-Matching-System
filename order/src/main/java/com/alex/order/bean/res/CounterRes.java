package com.alex.order.bean.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CounterRes {

    public static final int SUCCESS = 0;
    public static final int RELOGIN = 1;
    public static final int FAIL = 2;

    private int code;
    private String message;
    private Object data;

    public CounterRes (Object data) {
        this(0, "", data);
    }
}
