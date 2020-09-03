package thirdparty.tcp;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class TcpDirectSender {

    @NonNull
    private String ip;

    @NonNull
    private int port;

    @NonNull
    private Vertx vertx;

    //////////////////////////////////////////////////////////////////////////////


    private volatile NetSocket socket;

    public void startup() {
        vertx.createNetClient().connect(port, ip, new ClientConnHandler());

        new Thread(() -> {
            while (true) {
                try {
                    Buffer msgBuffer = sendCache.poll(5, TimeUnit.SECONDS);
                    if (msgBuffer != null
                            && msgBuffer.length() > 0
                            && socket != null) {
                        socket.write(msgBuffer);
                    }

                } catch (Exception e) {
                }
            }
        }).start();

    }

    //socket自己去缓存中取数据进行发送
    private final BlockingQueue<Buffer> sendCache = new LinkedBlockingDeque<>();

    public boolean send(Buffer bufferMsg) {
        return sendCache.offer(bufferMsg);
    }


    private class ClientConnHandler implements Handler<AsyncResult<NetSocket>> {

        private void reconnect() {
            vertx.setTimer(1000 * 5, r -> {
                vertx.createNetClient()
                        .connect(port, ip, new ClientConnHandler());
            });
        }

        @Override
        public void handle(AsyncResult<NetSocket> result) {
            if (result.succeeded()) {
                socket = result.result();

                socket.closeHandler(close -> {
                    //重连
                    reconnect();
                });

                socket.exceptionHandler(ex -> {
                });

            } else {

            }

        }
    }


}
