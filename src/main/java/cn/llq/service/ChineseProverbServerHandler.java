package cn.llq.service;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ThreadLocalRandom;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;


public class ChineseProverbServerHandler extends
        SimpleChannelInboundHandler<DatagramPacket> {
    static List<Map<String,String>>  list = new ArrayList<Map<String, String>>();
    static Integer next = 0;
    static Integer num = 0;
    /**
     * @param num1 分区数量 一般1个分区存放1000个以内
     *
     * */
    public static void  init (Integer num1) {
        num = num1;
        for (int i = 0; i < num; i++) {
            list.add(new Hashtable<String,String>());
        }
    }


    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    /**
     * 在这个方法中，形参packet客户端发过来的DatagramPacket对象
     * DatagramPacket 类解释
     * 1.官网是这么说的：
     * The message container that is used for {@link DatagramChannel} to communicate with the remote peer.
     * 翻译：DatagramPacket 是消息容器，这个消息容器被 DatagramChannel使用，作用是用来和远程设备交流
     * 2.看它的源码我们发现DatagramPacket是final类不能被继承，只能被使用。我们还发现DatagramChannel最终实现了AddressedEnvelope接口，接下来我们看一下AddressedEnvelope接口。
     * AddressedEnvelope接口官网解释如下：
     * A message that wraps another message with a sender address and a recipient address.
     * 翻译：这是一个消息,这个消息包含发送者和接受者消息
     * 3.那我们知道了DatagramPacket它包含了发送者和接受者的消息，
     * 通过content()来获取消息内容
     * 通过sender();来获取发送者的消息
     * 通过recipient();来获取接收者的消息。
     * 
     * 4.public DatagramPacket(ByteBuf data, InetSocketAddress recipient) {}
     *  这个DatagramPacket其中的一个构造方法，data 是发送内容;是发送都信息。
     */



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    public static synchronized Integer  getNext() {
        if (next<num) {
            next++;
        }else{
            next = 0;
        }
        return next;
    }
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        String req = packet.content().toString(CharsetUtil.UTF_8);//上面说了，通过content()来获取消息内容
        System.out.println(req);
//        if("谚语字典查询？".equals(req)){//如果消息是“谚语字典查询？”，就随机获取一条消息发送出去。
////            /**
////             * 重新 new 一个DatagramPacket对象，我们通过packet.sender()来获取发送者的消息。
////             * 重新发达出去！
////             */
////
////        }
        try{
            JSONObject message = (JSONObject) JSONObject.parse(req);
//            String hostName =  ctx.channel().remoteAddress().toString();
//            int port = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort();
            InetSocketAddress  remoteAddress = packet.sender();
            String ip = remoteAddress.getAddress().getHostAddress();
            int port = remoteAddress.getPort();
            message.put("ip",ip);
            message.put("port",port);
            Object uuid = message.get("UUID");
            System.out.println(message.get("div"));
            if (StringUtils.isEmpty(message.get("div"))){
                Integer next = getNext();
                message.put("div",next);
                list.get(next).put(uuid.toString(),message.toJSONString());
            }else{
                Integer div = message.getInteger("div");
                list.get(div).put(uuid.toString(),message.toJSONString());
            }

            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(message.toJSONString(),CharsetUtil.UTF_8), packet.sender()));

        }catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer("{err:\"消息有误\"}",CharsetUtil.UTF_8), packet.sender()));
        }


    }
}