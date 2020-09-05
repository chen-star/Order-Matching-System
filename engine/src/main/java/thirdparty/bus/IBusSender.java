package thirdparty.bus;

import thirdparty.bean.CommonMsg;

public interface IBusSender {
    
    void startup();
    
    void publish(CommonMsg commonMsg);
}
