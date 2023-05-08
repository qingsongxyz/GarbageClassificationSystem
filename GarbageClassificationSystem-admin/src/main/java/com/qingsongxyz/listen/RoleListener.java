package com.qingsongxyz.listen;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.util.RoleUtil;
import com.qingsongxyz.vo.PathVO;
import com.qingsongxyz.vo.RolePathVO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qingsongxyz.constant.RedisConstant.ROLE_PATH_KEY;

@Component
@Slf4j
public class RoleListener {

    private final StringRedisTemplate stringRedisTemplate;

    private final PathService pathService;

    public RoleListener(StringRedisTemplate stringRedisTemplate, PathService pathService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.pathService = pathService;
    }

    @RabbitListener(queues = {"role_queue"})
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
                String oldRole = beforeJsonObject.getString("role");
                String newRole = afterJsonObject.getString("role");
                //如果role表数据行role字段被修改
                if (!oldRole.equals(newRole)) {
                    //修改
                    log.info("role表update:{}|{}", beforeJsonObject, afterJsonObject);

                    List<PathVO> pathVOList = pathService.getAllPathDetails();
                    HashMap<String, String> map = new HashMap<>();
                    pathVOList.forEach(p -> {
                        List<RolePathVO> rolePathVOList = p.getRolePathVOList();
                        if(ObjectUtil.isNotEmpty(rolePathVOList)){
                            //权限路径对应的角色集合
                            List<String> list = new ArrayList<>();
                            rolePathVOList.forEach(r -> {
                                String role = r.getRoleVO().getRole();
                                list.add(role);
                            });
                            if(list.size() > 0) {
                                map.put(p.getPath(), JSON.toJSONString(list));
                            }
                        }
                    });

                    //更新redis
                    stringRedisTemplate.opsForHash().putAll(ROLE_PATH_KEY, map);
                }
            }

            //对消息进行确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}