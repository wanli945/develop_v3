package com.baibai.nettyserver.handler;

import com.baibai.nettyserver.maps.ContainerMaps;
import com.baibai.nettyserver.message.ParsingMsg;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.baibai.nettyserver.message.ParsingMsg.ParsingMessage;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    public static final String CHARSET = "UTF-8";
    public static AtomicInteger nConnection = new AtomicInteger(0);
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    public NettyServerHandler(){}

    /**
     * 该方法用于接收从客户端接收的信息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     * @time 2020:03:14 13:25:05
     * @see io.netty.channel.ChannelInboundHandlerAdapter#channelRead(io.netty.channel.ChannelHandlerContext, java.lang.Object)
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            /*//数据转换为string
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            String cmd = new String(bytes, 0, buf.readableBytes());
            log.info("收到消息: " + cmd);*/
            ByteBuf buf = (ByteBuf) msg;
            byte[] req = new byte[buf.readableBytes()];
            buf.readBytes(req);
            String message = new String(req, "GBK");
            System.out.println("服务器接收到消息：" + message);
            //将受到的消息进行切割
            //Channel channel = ctx.channel();
            //parsingMessage(cmd, channel);
            //报文分析
            ParsingMessage(message,ctx.channel());
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            ctx.flush();
            ReferenceCountUtil.release(msg);
        }
    }




    /**
     * @param ctx
     * @author wanli on 2017/5/20 16:10
     * @DESCRIPTION: 建立连接时，返回消息
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            //int i = nConnection.incrementAndGet();//连接数加一
            ctx.flush();
            super.channelActive(ctx);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * @param ctx
     * @author xiongchuan on 2017/5/20 16:10
     * @DESCRIPTION: 有客户端终止连接服务器会触发此函数
     * @return: void
     */
    /*@Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //nConnection.decrementAndGet();  //連接數减一
        Channel channel = ctx.channel();
        String IMEIID = GatewayService.getGatewayChannelX(channel);
        GatewayService.removeGatewayChannelX(channel);
        if (!MyUtils.isNull(IMEIID)) {
            GatewayService.removeGatewayChannel(IMEIID);
        }
        log.info(" 1当前设备已断开 设备ID为" + IMEIID + "当前设备连接数为: " + GatewayService.getChannelsX().size());
        ctx.flush();
        ctx.close();
    }*/

    /*@Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //这里执行客户端断开连接后的操作
        Channel channel = ctx.channel();
        String IMEIID = GatewayService.getGatewayChannelX(channel);
        GatewayService.removeGatewayChannelX(channel);
        if (!MyUtils.isNull(IMEIID)) {
            GatewayService.removeGatewayChannel(IMEIID);
        }
        log.info("2客户端断开 当前设备连接数为: " + GatewayService.getChannelsX().size());
        ctx.close();
    }*/

    /**
     * 往指定客户端发送指令
     *
     * @param cmd
     * @param ctx
     * @throws UnsupportedEncodingException
     */
    public void channelWrite(String cmd, ChannelHandlerContext ctx) throws UnsupportedEncodingException {
        byte[] respon = cmd.getBytes(CHARSET);
        //ByteBuf response = Unpooled.buffer(respon.length);
        PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        ByteBuf response = allocator.buffer(respon.length);
        final ByteBuf data = ctx.alloc().buffer(respon.length);
        data.writeBytes(respon);
        ctx.channel().writeAndFlush(data);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        String socketString = ctx.channel().remoteAddress().toString();

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                log.info("Client: " + socketString + " READER_IDLE 读超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.WRITER_IDLE) {
                log.info("Client: " + socketString + " WRITER_IDLE 写超时");
                ctx.disconnect();
            } else if (event.state() == IdleState.ALL_IDLE) {
                log.info("Client: " + socketString + " ALL_IDLE 总超时");
                ctx.disconnect();
            }
        }
    }

    /**
     * channelReadComplete channel 通道 Read 读取 Complete 完成
     * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().isActive();
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Map<String, Channel> channels = ContainerMaps.getChannels();
        Map<Channel, String> channelsX = ContainerMaps.getChannelsX();
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if(channel.isActive())ctx.close();
        String IMEIID = ContainerMaps.getGatewayChannelX(channel);
        log.info(" 3当前设备已断开 设备ID为" + IMEIID + "当前设备连接数为: " + ContainerMaps.getChannelsX().size());
        if (IMEIID!=null) {
            if (channels.containsKey(IMEIID)) {
                ContainerMaps.removeGatewayChannel(IMEIID);
            }
            if (channelsX.containsKey(channel)) {
                ContainerMaps.removeGatewayChannelX(channel);
            }
        }
        cause.printStackTrace();
    }
}
