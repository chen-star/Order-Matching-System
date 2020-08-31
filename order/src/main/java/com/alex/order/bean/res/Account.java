package com.alex.order.bean.res;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Account {

    @NonNull
    private int id;

    @NonNull
    private long uid;

    private String lastLoginDate;

    private String lastLoginTime;

    private String token;

}