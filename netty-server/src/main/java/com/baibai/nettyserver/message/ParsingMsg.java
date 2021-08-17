package com.baibai.nettyserver.message;

import com.baibai.nettyserver.maps.ContainerMaps;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ParsingMsg {

    /**
     * 解析报文
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    public static void ParsingMessage(String message, Channel channel) throws Exception {
        //服务器接收到消息后进行解析
        String[] msg = message.split("");
        if (msg[0].equals("_")) {
            //格式标识规范"_"
            Map<Channel, String> channelsX = ContainerMaps.getChannelsX();

            if (channelsX.containsKey(channel)) {
                String IMEIID = channelsX.get(channel);
                //保存起来
//                if (redisService.hasKeyEncryption(IMEIID + "_PING")) {
//                    //存在
//                    String signal = redisService.getEncryption(IMEIID + "_PING").toString();
//                    redisService.expire(IMEIID + "_PING", signal, 180);
//                }
            }
            String type = msg[1] + msg[2];
//            if (type.equals("PG")) {
//                //PING消息处理
//                PG_Handler(cmd, channel);
//            }
//            if (type.equals("DV")) {
//                //_DVADV000000019IM15860344044237700
//                //返回设备号
//                ADV_Handler(cmd, channel);
//            }
//            if (type.equals("ID")) {
//                //返回ICCID号
//                AID_Handler(cmd, channel);
//            }
//            if (type.equals("RP")) {
//                //数据上报
//                RP_Handler(cmd, channel);
//            }
//            if (type.equals("RS")) {
//                RS_Handler(cmd, channel);
//            }
        } else {
            log.info("非法报文：" + message);
        }
    }

}
