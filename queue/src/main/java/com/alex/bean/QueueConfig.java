package com.alex.bean;

import com.alipay.sofa.jraft.rhea.options.PlacementDriverOptions;
import com.alipay.sofa.jraft.rhea.options.RheaKVStoreOptions;
import com.alipay.sofa.jraft.rhea.options.StoreEngineOptions;
import com.alipay.sofa.jraft.rhea.options.configured.MemoryDBOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.PlacementDriverOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.RheaKVStoreOptionsConfigured;
import com.alipay.sofa.jraft.rhea.options.configured.StoreEngineOptionsConfigured;
import com.alipay.sofa.jraft.rhea.storage.StorageType;
import com.alipay.sofa.jraft.util.Endpoint;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.listener.ChannelListener;
import com.alipay.sofa.rpc.transport.AbstractChannel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.vertx.core.Vertx;
import io.vertx.core.datagram.DatagramSocket;
import io.vertx.core.datagram.DatagramSocketOptions;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import thirdparty.codec.IBodyCodec;
import thirdparty.fetchsurv.IFetchService;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

@ToString
@RequiredArgsConstructor
public class QueueConfig {


    private String dataPath;

    private String serveUrl;

    private String serverList;

    @Getter
    private String multicastIp;

    @Getter
    private int multicastPort;

    @Getter
    private DatagramSocket multicastSender;


    @NonNull
    private String fileName;

    public void startup() throws Exception {
        //1.读取配置文件
        initConfig();

        //2.初始化kv store集群
        startSeqDbCluster();

        //3.启动下游广播
        startMulticast();

        //TODO 4.初始化网关连接
        startupFetch();


    }


    /////////////////////////////Multicast/////////////////////////////////////////////

    private void startMulticast() {
        multicastSender = Vertx.vertx().createDatagramSocket(new DatagramSocketOptions());
    }


    /////////////////////////////抓取逻辑/////////////////////////////////////////////

    private String fetchurls;

    @ToString.Exclude
    @Getter
    private Map<String, IFetchService> fetchServiceMap = Maps.newConcurrentMap();

    @NonNull
    @ToString.Exclude
    @Getter
    private IBodyCodec codec;

    @RequiredArgsConstructor
    private class FetchChannelListener implements ChannelListener {

        @NonNull
        private ConsumerConfig<IFetchService> config;


        @Override
        public void onConnected(AbstractChannel channel) {
            String remoteAddr = channel.remoteAddress().toString();
            System.out.println("connect to gateway : " + remoteAddr);
            fetchServiceMap.put(remoteAddr, config.refer());
        }

        @Override
        public void onDisconnected(AbstractChannel channel) {
            String remoteAddr = channel.remoteAddress().toString();
            System.out.println("disconnect to gateway : " + remoteAddr);
            fetchServiceMap.remove(remoteAddr);
        }
    }

    //1.从哪些网关抓取
    //2.通信方式
    private void startupFetch() {
        //1.建立所有到网关的连接
        String[] urls = fetchurls.split(";");
        for (String url : urls) {
            ConsumerConfig<IFetchService> consumerConfig = new ConsumerConfig<IFetchService>()
                    .setInterfaceId(IFetchService.class.getName())//通信接口
                    .setProtocol("bolt")//RPC通信协议
                    .setTimeout(5000)//超时时间
                    .setDirectUrl(url);//直连地址
            consumerConfig.setOnConnect(Lists.newArrayList(new FetchChannelListener(consumerConfig)));
            fetchServiceMap.put(url, consumerConfig.refer());
        }

        //2.定时抓取数据的任务
        new Timer().schedule(new FetchTask(this), 5000, 1000);
    }


    //////////////////////////////////////////////////////////////////////////

    @Getter
    private Node node;

    //启动KV Store
    private void startSeqDbCluster() {
        final PlacementDriverOptions pdOpts = PlacementDriverOptionsConfigured.newConfigured()
                .withFake(true)
                .config();


        // 127.0.0.1:8891
        String[] split = serveUrl.split(":");
        final StoreEngineOptions storeOpts = StoreEngineOptionsConfigured.newConfigured()
                .withStorageType(StorageType.Memory)
                .withMemoryDBOptions(MemoryDBOptionsConfigured.newConfigured().config())
                .withRaftDataPath(dataPath)
                .withServerAddress(new Endpoint(split[0], Integer.parseInt(split[1])))
                .config();

        final RheaKVStoreOptions opts = RheaKVStoreOptionsConfigured.newConfigured()
                .withInitialServerList(serverList)
                .withStoreEngineOptions(storeOpts)
                .withPlacementDriverOptions(pdOpts)
                .config();

        node = new Node(opts);
        node.start();
        Runtime.getRuntime().addShutdownHook(new Thread(node::stop));
        System.out.println("start seq node success on port : " + split[1]);

    }

    private void initConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(Object.class.getResourceAsStream("/" + fileName));

        dataPath = properties.getProperty("datapath");
        serveUrl = properties.getProperty("serveurl");
        serverList = properties.getProperty("serverlist");
        fetchurls = properties.getProperty("fetchurls");
        multicastIp = properties.getProperty("multicastIp");
        multicastPort = Integer.parseInt(properties.getProperty("multicastport"));
    }


}

