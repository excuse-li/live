package cn.llq.server;


import cn.llq.service.ChineseProverbServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class ChineseProverbServer {
    public void run(int port){
        new Thread(){
            @Override
            public void run() {
                EventLoopGroup group = new NioEventLoopGroup();
                Bootstrap b = new Bootstrap();
                //由于我们用的是UDP协议，所以要用NioDatagramChannel来创建
                b.group(group).channel(NioDatagramChannel.class)
                        .option(ChannelOption.SO_BROADCAST, true)//支持广播
                        .handler(new ChineseProverbServerHandler());//ChineseProverbServerHandler是业务处理类
                try{
                    b.bind(port).sync().channel().closeFuture().await();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.start();

    }
    public ChineseProverbServer (Integer port){
        try {
            this.run(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
