package com.xue;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xuejingbao
 * @create 2023-05-23 13:59
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 加载配置文件
        Config.INSTANCE.loadConfig(args);

        // 开启与router之间的交互
        UdpSender.init();
        // 开启位置信息获取
        MqttLocation.INSTANCE.init();
        // 开启位置信息发送，并设置为守护进程
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(false).build());
        executorService.scheduleAtFixedRate(new GpsSender(), 2, 1, TimeUnit.SECONDS);
    }

}
