package com.alex.gateway.bean;

import com.alex.gateway.bean.handler.ConnHandler;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import thirdparty.checksum.ICheckSum;
import thirdparty.codec.IBodyCodec;
import thirdparty.fetchsurv.IFetchService;

import java.io.File;

@Getter
public class GatewayConfig {

    // Gateway Id
    private short id;

    // Port
    private int recvPort;


    private int fetchServPort;

    @Setter
    private IBodyCodec bodyCodec;

    @Setter
    private ICheckSum cs;

    private Vertx vertx = Vertx.vertx();


    public void initConfig(String fileName) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(fileName));
        Element root = document.getRootElement();

        // Port
        id = Short.parseShort(root.element("id").getText());
        recvPort = Integer.parseInt(root.element("recvport").getText());
        System.out.println("GateWay ID:" + id + " Port: " + recvPort);

        fetchServPort = Integer.parseInt(root.element("fetchservport").getText());

        //TODO 数据库连接 连接柜台列表

    }

    public void startup() throws Exception {
        //1.启动TCP服务监听
        initRecv();

        //2.排队机交互
        initFetchServ();
    }

    private void initFetchServ() {
        ServerConfig rpcConfig = new ServerConfig()
                .setPort(fetchServPort)
                .setProtocol("bolt");

        ProviderConfig<IFetchService> providerConfig = new ProviderConfig<IFetchService>()
                .setInterfaceId(IFetchService.class.getName())
                .setRef(() -> OrderCmdContainer.getInstance().getAll())
                .setServer(rpcConfig);
        providerConfig.export();
    }

    private void initRecv() {
        NetServer server = vertx.createNetServer();
        server.connectHandler(new ConnHandler(this));
        server.listen(recvPort, res -> {
            if (res.succeeded()) {
                System.out.println("gateway startup success at port :" + recvPort);
            } else {
                System.out.println("gateway startup fail");
            }
        });
    }
}

