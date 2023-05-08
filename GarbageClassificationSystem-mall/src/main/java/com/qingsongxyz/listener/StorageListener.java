package com.qingsongxyz.listener;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

import static com.qingsongxyz.constant.RedisConstant.STORAGE_LIST_KEY;

@Component
@Slf4j
public class StorageListener {

    private final StringRedisTemplate stringRedisTemplate;

    public StorageListener(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @RabbitListener(queues = {"storage_queue"})
    public void receive(ByteString storeValue, Message message, Channel channel) throws IOException {
        //反序列化数据
        CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(storeValue);

        //获取entry类型
        CanalEntry.EventType eventType = rowChange.getEventType();

        //获取数据集
        List<CanalEntry.RowData> rowDataList = rowChange.getRowDatasList();

        //对于insert操作没有前数据,delete操作没有后数据,修改操作有前后数据
        for (CanalEntry.RowData rowData : rowDataList) {
            //更新之前的数据
            List<CanalEntry.Column> beforeColumnsList = rowData.getBeforeColumnsList();

            //更新之后的数据
            List<CanalEntry.Column> afterColumnsList = rowData.getAfterColumnsList();

            JSONObject beforeJsonObject = new JSONObject();

            JSONObject afterJsonObject = new JSONObject();

            for (CanalEntry.Column column : beforeColumnsList) {
                beforeJsonObject.put(column.getName(), column.getValue());
            }

            for (CanalEntry.Column column : afterColumnsList) {
                afterJsonObject.put(column.getName(), column.getValue());
            }

            String goodId = afterJsonObject.getString("good_id");

            switch (eventType) {
                case INSERT:
                    log.info("storage表insert:{}", afterJsonObject);
                    int storage = afterJsonObject.getInteger("storage");
                    stringRedisTemplate.opsForValue().set(STORAGE_LIST_KEY + ":" + goodId, String.valueOf(storage));
                    break;
                case UPDATE:
                    //逻辑删除
                    if (afterJsonObject.getInteger("deleted") == 1) {
                        log.info("storage表delete:{}", afterJsonObject);
                        //删除库存缓存
                        stringRedisTemplate.delete(STORAGE_LIST_KEY + ":" + goodId);
                        break;
                    }
                    int oldStorage = beforeJsonObject.getInteger("storage");
                    int newStorage = afterJsonObject.getInteger("storage");
                    //商品库存被修改
                    if (oldStorage != newStorage) {
                        //修改
                        log.info("storage表update:{}|{}", beforeJsonObject, afterJsonObject);
                        //更新redis缓存
                        stringRedisTemplate.opsForValue().set(STORAGE_LIST_KEY + ":" + goodId, String.valueOf(newStorage));
                    }
                    break;
            }

            //对消息进行确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
