package com.alex.order.service;

import com.alex.order.bean.res.Account;
import com.alex.order.cache.CacheType;
import com.alex.order.cache.RedisStringCache;
import com.alex.order.util.DbUtil;
import com.alex.order.util.JsonUtil;
import com.alex.order.util.TimeFormatUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import thirdparty.uuid.UuidGenerator;

import java.util.Date;

@Service
@Log4j2
public class AccountServiceImpl implements AccountService {

    @Override
    public Account login(long uid, String password, String captcha, String captchaId) throws Exception {
        if (StringUtils.isAnyBlank(password, captcha,
                captchaId)) {
            return null;
        }

        // get verification code from cache
        String captchaCache =
                RedisStringCache.get(captchaId, CacheType.CAPTCHA);
        if (StringUtils.isEmpty(captchaCache)) {
            return null;
        } else if (!StringUtils.equalsIgnoreCase(captcha, captchaCache)) {
            return null;
        }
        RedisStringCache.remove(captchaId, CacheType.CAPTCHA);

        // verify account in DB
        Account account = DbUtil.queryAccount(uid, password);
        if (account == null) {
            return null;
        } else {
            // set uuid
            account.setToken(String.valueOf(
                    UuidGenerator.getInstance().getUUID()
            ));

            // add account into cache
            RedisStringCache.cache(String.valueOf(
                    account.getToken()), JsonUtil.toJson(account),
                    CacheType.ACCOUNT
            );

            // update lastLogin
            Date date = new Date();
            DbUtil.updateLoginTime(uid,
                    TimeFormatUtil.yyyyMMdd(date),
                    TimeFormatUtil.hhMMss(date));

            return account;
        }
    }

    @Override
    public boolean accountExistInCache(String token) {
        if (StringUtils.isBlank(token)) {
            return false;
        }

        // get account from cache
        String acc = RedisStringCache.get(token, CacheType.ACCOUNT);
        if (acc != null) {
            RedisStringCache.cache(token, acc, CacheType.ACCOUNT);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean logout(String token) {
        RedisStringCache.remove(token,CacheType.ACCOUNT);
        return true;
    }

//    @Override
//    public boolean updatePwd(long uid, String oldPwd, String newPwd) {
//        int res = DbUtil.updatePwd(uid,oldPwd,newPwd);
//
//        return res == 0 ? false : true;
//    }
}
