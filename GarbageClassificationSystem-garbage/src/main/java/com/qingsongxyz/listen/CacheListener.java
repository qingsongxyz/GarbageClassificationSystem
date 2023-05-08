package com.qingsongxyz.listen;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import com.qingsongxyz.mapper.GarbageMapper;
import com.qingsongxyz.pojo.Garbage;
import com.qingsongxyz.vo.GarbageVO;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.qingsongxyz.constant.RedisConstant.GARBAGE_LIST_KEY;
import static com.qingsongxyz.constant.RedisConstant.GARBAGE_LIST_KEY_TTL_SECOND;
import static com.qingsongxyz.service.impl.GarbageServiceImpl.INDEX_NAME;

@Component
@Slf4j
public class CacheListener {

    private final StringRedisTemplate stringRedisTemplate;

    private final RestHighLevelClient restHighLevelClient;

    private final GarbageMapper garbageMapper;

    public CacheListener(StringRedisTemplate stringRedisTemplate, RestHighLevelClient restHighLevelClient, GarbageMapper garbageMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.restHighLevelClient = restHighLevelClient;
        this.garbageMapper = garbageMapper;
    }

    @RabbitListener(queues = {"garbage_queue"})
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

            long randomTime = RandomUtil.randomLong(1, 30);

            Map<String, String> map = null;
            GarbageVO garbageVO = null;

            switch (eventType) {
                case INSERT:
                    //添加
                    log.info("garbage表insert:{}", afterJsonObject);
                    map = getMap(afterJsonObject.get("id"));
                    garbageVO = getGarbageVO(afterJsonObject.get("id"));

                    //添加到redis缓存
                    stringRedisTemplate.opsForHash().putAll(GARBAGE_LIST_KEY + ":" + afterJsonObject.getString("id"), map);
                    stringRedisTemplate.expire(GARBAGE_LIST_KEY + ":" + afterJsonObject.getString("id"), GARBAGE_LIST_KEY_TTL_SECOND + randomTime, TimeUnit.SECONDS);

                    //添加到elasticSearch
                    IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
                    indexRequest
                            .id(garbageVO.getId().toString())
                            .source(JSON.toJSONString(garbageVO), XContentType.JSON);
                    IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                    log.info("添加文档{}是否成功:{}", garbageVO.getId(), indexResponse.status());

                    break;
                case UPDATE:
                    //逻辑删除
                    if(afterJsonObject.getInteger("deleted") == 1)
                    {
                        log.info("garbage表delete:{}", afterJsonObject);
                        //从redis缓存中删除
                        stringRedisTemplate.delete(GARBAGE_LIST_KEY + ":" + afterJsonObject.getString("id"));

                        //从elasticSearch中删除
                        DeleteRequest deleteRequest = new DeleteRequest(INDEX_NAME, afterJsonObject.getString("id"));
                        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
                        log.info("删除文档{}是否成功:{}", afterJsonObject.getString("id"), deleteResponse.status());
                        break;
                    }
                    //修改
                    log.info("garbage表update:{}|{}", beforeJsonObject, afterJsonObject);
                    map = getMap(afterJsonObject.get("id"));
                    garbageVO = getGarbageVO(afterJsonObject.get("id"));

                    //更新redis缓存
                    stringRedisTemplate.opsForHash().putAll(GARBAGE_LIST_KEY + ":" + afterJsonObject.getString("id"), map);
                    stringRedisTemplate.expire(GARBAGE_LIST_KEY + ":" + afterJsonObject.getString("id"), GARBAGE_LIST_KEY_TTL_SECOND + randomTime, TimeUnit.SECONDS);

                    //更新elasticSearch
                    UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, afterJsonObject.getString("id"));
                    updateRequest.doc(JSON.toJSONString(garbageVO), XContentType.JSON);
                    UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
                    log.info("修改文档{}是否成功:{}", garbageVO.getId(), updateResponse.status());
                    break;
            }

            //对消息进行确认
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
    }

    /**
     * 通过id查询垃圾信息,封装成 VO对象
     * @param id 垃圾id
     * @return GarbageVO对象
     */
    private GarbageVO getGarbageVO(Object id){
        long param = 0L;
        if(id instanceof Long){
            param = (Long)id;
        }else if(id instanceof String){
            param = Long.parseLong((String) id);
        }
        Garbage garbage = garbageMapper.getGarbageById(param);
        String category = garbage.getGarbageCategory().getName();
        GarbageVO garbageVO = BeanUtil.copyProperties(garbage, GarbageVO.class);
        garbageVO.setCategory(category);
        return garbageVO;
    }

    /**
     * 通过id查询垃圾信息,封装成Map
     * @param id 垃圾id
     * @return map
     */
    private Map<String, String> getMap(Object id) {
        long param = 0L;
        if(id instanceof Long){
            param = (Long)id;
        }else if(id instanceof String){
            param = Long.parseLong((String) id);
        }
        Garbage garbage = garbageMapper.getGarbageById(param);
        String category = garbage.getGarbageCategory().getName();
        HashMap<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(id));
        map.put("name", garbage.getName());
        map.put("category", category);
        map.put("unit", garbage.getUnit());
        map.put("score", garbage.getScore().toString());
        return map;
    }
}
