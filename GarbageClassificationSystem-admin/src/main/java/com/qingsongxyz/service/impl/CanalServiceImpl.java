package com.qingsongxyz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.exception.CanalClientException;
import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static com.qingsongxyz.config.rabbitmq.PathCacheConfig.*;
import static com.qingsongxyz.config.rabbitmq.UserRoleConfig.USER_ROLE_EXCHANGE_NAME;
import static com.qingsongxyz.config.rabbitmq.UserRoleConfig.USER_ROLE_ROUTING_KEY;

@Service
@Slf4j
public class CanalServiceImpl {

    //一次拿取的数量
    private static final int BATCH_SIZE = 1000;

    private final CanalConnector canalConnector;

    private final RabbitTemplate rabbitTemplate;

    public CanalServiceImpl(CanalConnector canalConnector, RabbitTemplate rabbitTemplate) {
        this.canalConnector = canalConnector;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Async
    public void run() throws InterruptedException {
        log.info("异步任务启动...");
        while (true) {
            //1.获取数据
            //尝试拿数据,有多少取多少,不会阻塞等待
            Message message = canalConnector.getWithoutAck(BATCH_SIZE);

            //获取消息的id进行确认或回滚
            long batchId = message.getId();

            //2.获取Entry集合
            List<CanalEntry.Entry> entries = message.getEntries();

            if (batchId == -1 || !ObjectUtils.isEmpty(entries)) {
                try {
                    //2.1 遍历entries
                    for (CanalEntry.Entry entry : entries) {
                        //获取entry类型
                        CanalEntry.EntryType entryType = entry.getEntryType();
                        String tableName = entry.getHeader().getTableName();

                        //判断entry是否属于tableList中的表并且类型为ROWDATA,传入消息队列
                        List<String> tableList = new ArrayList<>(3);
                        tableList.add("role");
                        tableList.add("path");
                        tableList.add("role_path");
                        if (tableList.contains(tableName) && CanalEntry.EntryType.ROWDATA.equals(entryType)) {
                            //获取序列化后的数据
                            ByteString storeValue = entry.getStoreValue();
                            log.info("投放数据:{}", storeValue);
                            String routingKey = null;
                            if ("role_path".equals(tableName)) {
                                routingKey = ROLE_PATH_ROUTING_KEY;
                            } else if ("role".equals(tableName)) {
                                routingKey = ROLE_ROUTING_KEY;
                            } else {
                                routingKey = PATH_ROUTING_KEY;
                            }

                            rabbitTemplate.convertAndSend(
                                    ROLE_PATH_EXCHANGE_NAME,
                                    routingKey,
                                    storeValue,
                                    m -> {
                                        m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                                        return m;
                                    },
                                    new CorrelationData(IdUtil.simpleUUID()));
                        }
                    }

                    //进行 batch id 的确认,确认之后小于等于此 batchId 的 Message 都会被确认
                    canalConnector.ack(batchId);
                } catch (Exception e) {
                    log.error("gcs-admin——监听出现异常:{}, canal消息回滚...", e.getMessage());
                    canalConnector.rollback(batchId);
                }
            } else {
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }
    }
}
