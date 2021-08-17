package com.baibai.nettyserver.filter;

import com.baibai.nettyserver.handler.NettyServerHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * Title: NettyServerFilter
 * Description:
 * Netty 服务端过滤器
 * Version:1.0.0
 *
 * @author wanli
 * @date 2017-5-22
 */
public class NettyServerInitFilter extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline ph = ch.pipeline();
        //PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
        // 以("\n")为结尾分割的 解码器
        //ph.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.nulDelimiter()));
        // 解码和编码，应和客户端一致
        //ph.addLast(new LineBasedFrameDecoder(1024));	//字节解码器 ,其中2048是规定一行数据最大的字节数。  用于解决拆包问题
        //ph.addLast(new FixedLengthFrameDecoder(100));   //定长数据帧的解码器 ，每帧数据100个字节就切分一次。  用于解决粘包问题
        //ph.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
        ph.addLast(new DelimiterBasedFrameDecoder(10240, Unpooled.copiedBuffer("\r\n".getBytes()))); //固定字符切分解码器 ,会以"~_~"为分隔符。  注意此方法要放到StringDecoder()上面
        //ph.addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        //ph.addLast("encoder", new LengthFieldPrepender(4, false));
        ph.addLast(new NettyServerHandler());
        //ph.addLast("encoder", new StringEncoder());
    }
}
