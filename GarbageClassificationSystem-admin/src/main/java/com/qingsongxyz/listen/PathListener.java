package com.qingsongxyz.listen;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.util.RoleUtil;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import static com.qingsongxyz.constant.RedisConstant.ROLE_PATH_KEY;

@Component
@Slf4j
public class PathListener {

    private final StringRedisTemplate stringRedisTemplate;

    private final PathService pathService;

    public PathListener(StringRedisTemplate stringRedisTemplate, PathService pathService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.pathService = pathService;
    }

    @RabbitListener(queues = {"path_queue"})
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

            if(eventType.equals(CanalEntry.EventType.UPDATE)){
                String oldPath = beforeJsonObject.getString("path");
                String newPath = afterJsonObject.getString("path");
                //如果path表数据行path字段被修改
                if (!oldPath.equals(newPath)) {
                    //修改
                    log.info("path表update:{}|{}", beforeJsonObject, afterJsonObject);

                    String roleList = RoleUtil.getRoleList(pathService, afterJsonObject.getLong("id"));
                    //更新redis缓存
                    stringRedisTemplate.opsForHash().delete(ROLE_PATH_KEY, oldPath);
                    stringRedisTemplate.opsForHash().put(ROLE_PATH_KEY, newPath, roleList);
                }
            }

            //对消息进行确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
