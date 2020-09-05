package thirdparty.codec;

import io.vertx.core.buffer.Buffer;
import thirdparty.bean.CommonMsg;

public interface IMsgCodec {

    //TCP <--> CommonMsg
    Buffer encodeToBuffer(CommonMsg msg);

    CommonMsg decodeFromBuffer(Buffer buffer);

}
