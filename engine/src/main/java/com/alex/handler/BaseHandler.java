package com.alex.handler;

import com.alex.bean.command.RbCmd;
import com.lmax.disruptor.EventHandler;

public abstract class BaseHandler implements EventHandler<RbCmd> {
}
