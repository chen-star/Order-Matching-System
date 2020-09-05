package com.alex;

import com.alex.bean.EngineConfig;
import thirdparty.checksum.ByteCheckSum;
import thirdparty.codec.BodyCodec;
import thirdparty.codec.MsgCodec;

public class EngineStartup {

    public static void main(String[] args) throws Exception {
        new EngineConfig(
                "engine.properties",
                new BodyCodec(),
                new ByteCheckSum(),
                new MsgCodec()
        ).startup();
    }
}
