package com.alex.handler.risk;

import com.alex.bean.command.CmdResultCode;
import com.alex.bean.command.RbCmd;
import com.alex.handler.BaseHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import thirdparty.order.CmdType;

@Log4j2
@RequiredArgsConstructor
public class ExistRiskHandler extends BaseHandler {

    @NonNull
    private MutableLongSet uidSet;

    @NonNull
    private MutableSet codeSet;

    //发布行情Event
    //新委托event
    //撤单event
    //权限控制，系统关机。。。
    @Override
    public void onEvent(RbCmd cmd, long sequence, boolean endOfBatch) throws Exception {

        //系统内部的指令，前置风控模块直接忽略
        if (cmd.command == CmdType.HQ_PUB) {
            return;
        }


        if (cmd.command == CmdType.NEW_ORDER || cmd.command == CmdType.CANCEL_ORDER) {
            //1.用户是否存在

            if (!uidSet.contains(cmd.uid)) {
                log.error("illegal uid[{}] exist", cmd.uid);
                cmd.resultCode = CmdResultCode.RISK_INVALID_USER;
                return;
            }

            //2.股票代码是否合法
            if (!codeSet.contains(cmd.code)) {
                log.error("illegal stock[{}] exist", cmd.code);
                cmd.resultCode = CmdResultCode.RISK_INVALID_CODE;
                return;
            }

        }


    }
}