package com.alex.order.controller;

import com.alex.order.bean.res.Account;
import com.alex.order.bean.res.CaptchaRes;
import com.alex.order.bean.res.CounterRes;
import com.alex.order.cache.CacheType;
import com.alex.order.cache.RedisStringCache;
import com.alex.order.service.AccountService;
import com.alex.order.util.Captcha;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import thirdparty.uuid.UuidGenerator;

import static com.alex.order.bean.res.CounterRes.FAIL;
import static com.alex.order.bean.res.CounterRes.RELOGIN;

@RestController
@RequestMapping("/login")
@Log4j2
public class LoginController {

    @Autowired
    private AccountService accountService;

    @RequestMapping("/captcha")
    public CounterRes captcha() throws Exception {
        // 1. generate verification code image
        Captcha captcha = new Captcha(120, 40, 4, 10);

        // 2. uuid
        String uuid = String.valueOf(UuidGenerator.getInstance().getUUID());
        RedisStringCache.cache(uuid, captcha.getCode(), CacheType.CAPTCHA);

        // 3. return base64 img & uuid
        CaptchaRes res = new CaptchaRes(uuid, captcha.getBase64ByteStr());
        return new CounterRes(res);
    }

    @RequestMapping("/userlogin")
    public CounterRes login(@RequestParam long uid,
                            @RequestParam String password,
                            @RequestParam String captcha,
                            @RequestParam String captchaId) throws Exception {

        Account account = accountService.login(uid, password,
                captcha, captchaId);

        if (account == null) {
            return new CounterRes(FAIL,
                    "Username / Password / Captcha wrong, Login failed", null);
        } else {
            return new CounterRes(account);
        }
    }

    @RequestMapping("/loginfail")
    public CounterRes loginFail(){
        return new CounterRes(RELOGIN,"Retry please",null);
    }
}
