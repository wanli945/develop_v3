package com.baibai.nettyserver.maps;

import io.netty.channel.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备MAP维护
 *
 * @program: boot-parent
 * @author: Mr.WanLi
 * @create: 2021-08-01 10:44
 **/
public class ContainerMaps {

    public static Map<String, Channel> map = new ConcurrentHashMap<>();
    public static Map<Channel, String> mapX = new ConcurrentHashMap<>();

    public static void addGatewayChannel(String id, Channel channel) {
        map.put(id, channel);
    }

    public static Map<String, Channel> getChannels() {
        return map;
    }

    public static Object getGatewayChannel(String id) {
        return map.get(id);
    }

    public static void removeGatewayChannel(String id) {
        map.remove(id);
    }

    public static Integer getChannelsSize() {
        return map.size();
    }


    /*---------------------------------------------------------------------------------------------*/

    //mapX维护
    public static void addGatewayChannelX(Channel channel, String id) {
        mapX.put(channel, id);
    }

    public static Map<Channel, String> getChannelsX() {
        return mapX;
    }

    public static String getGatewayChannelX(Channel channel) {
        return mapX.get(channel);
    }

    public static void removeGatewayChannelX(Channel channel) {
        mapX.remove(channel);
    }

}
