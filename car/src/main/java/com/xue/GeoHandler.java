package com.xue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xuejingbao
 * @create 2023-05-24 10:25
 */
@Slf4j
@ChannelHandler.Sharable
public class GeoHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        ByteBuf content = msg.content();
        byte aByte = content.getByte(0);
        int readableBytes = content.readableBytes();
        if (readableBytes > 101){
            if (log.isDebugEnabled()){
                log.debug("不符合标准长度包，丢弃：{}",ByteBufUtil.hexDump(content));
            }
            return;
        }
        byte[] contentByte = new byte[readableBytes];
        content.readBytes(contentByte);
        switch (aByte) {
            case 1:
                SimpleDenm simpleDenm = new SimpleDenm(contentByte);
                if (!Config.INSTANCE.getStationId().equals(simpleDenm.stationId)) {
                    log.info("收到别人的denm，亮灯:{},{}", simpleDenm.longitude, simpleDenm.latitude);
                    // LightControl.INSTANCE.lightClose();
                } else {
                    if (log.isDebugEnabled()){
                        log.debug("收到自己的denm:{},{}", simpleDenm.longitude, simpleDenm.latitude);
                    }
                }
                break;
            case 2:
                SimpleCam simpleCam = new SimpleCam(contentByte);
                if (log.isDebugEnabled()){
                    log.debug("收到cam:{},{}", simpleCam.longitude, simpleCam.latitude);
                }
                break;
            default:
                break;
        }
    }

}
