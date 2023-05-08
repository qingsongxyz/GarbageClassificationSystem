package com.qingsongxyz.listen;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.service.RolePathService;
import com.qingsongxyz.util.RoleUtil;
import com.qingsongxyz.vo.RolePathVO;
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
public class RolePathListener {

    private final StringRedisTemplate stringRedisTemplate;

    private final RolePathService rolePathService;

    private final PathService pathService;

    public RolePathListener(StringRedisTemplate stringRedisTemplate, RolePathService rolePathService, PathService pathService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rolePathService = rolePathService;
        this.pathService = pathService;
    }

    @RabbitListener(queues = {"role_path_queue"})
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

            RolePathVO rolePathVO = getRolePathVO(afterJsonObject.get("id"));
            //hash结构 对应的key value
            String path = "";
            if (ObjectUtil.isNotNull(rolePathVO.getPathVO())) {
                path = rolePathVO.getPathVO().getPath();
            }
            String roleList = RoleUtil.getRoleList(pathService, rolePathVO.getPathId());

            switch (eventType) {
                case INSERT:
                    //添加
                    log.info("role_path表insert:{}", afterJsonObject);
                    break;
                case UPDATE:
                    //逻辑删除
                    if (afterJsonObject.getInteger("deleted") == 1) {
                        log.info("role_path表delete:{}", afterJsonObject);
                        break;
                    }
                    //修改
                    log.info("role_path表update:{}|{}", beforeJsonObject, afterJsonObject);
                    break;
            }

            if (ObjectUtil.isNotEmpty(roleList)) {
                //更新redis缓存
                stringRedisTemplate.opsForHash().put(ROLE_PATH_KEY, path, roleList);
            } else {
                //删除对应的权限信息
                stringRedisTemplate.opsForHash().delete(ROLE_PATH_KEY, path);
            }

            //对消息进行确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 通过id查询角色权限信息
     *
     * @param id 角色权限id
     * @return 角色权限信息
     */
    private RolePathVO getRolePathVO(Object id) {
        long param = 0L;
        if (id instanceof Long) {
            param = (Long) id;
        } else if (id instanceof String) {
            param = Long.parseLong((String) id);
        }
        return rolePathService.getRolePathById(param);
    }

}
