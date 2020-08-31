package com.alex.order.service;

import com.alex.order.bean.res.Account;

public interface AccountService {

    Account login(long uid, String password, String captcha, String captchaId) throws Exception;

    boolean accountExistInCache(String token);

    boolean logout(String token);
}
