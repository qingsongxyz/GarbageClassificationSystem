package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.qingsongxyz.pojo.Garbage;
import com.qingsongxyz.mapper.GarbageMapper;
import com.qingsongxyz.service.GarbageCategoryService;
import com.qingsongxyz.service.GarbageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.vo.GarbageCategoryVO;
import com.qingsongxyz.vo.GarbageVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.qingsongxyz.constant.RedisConstant.GARBAGE_LIST_KEY;
import static com.qingsongxyz.constant.RedisConstant.GARBAGE_LIST_KEY_TTL_SECOND;

/**
 * <p>
 * 垃圾信息表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Service
@Slf4j
public class GarbageServiceImpl extends ServiceImpl<GarbageMapper, Garbage> implements GarbageService {

    private final GarbageMapper garbageMapper;

    private final GarbageCategoryService garbageCategoryService;

    private final StringRedisTemplate stringRedisTemplate;

    private final RestHighLevelClient restHighLevelClient;

    public static final String INDEX_NAME = "gcs_garbage";

    public GarbageServiceImpl(GarbageMapper garbageMapper, GarbageCategoryService garbageCategoryService, StringRedisTemplate stringRedisTemplate, RestHighLevelClient restHighLevelClient) {
        this.garbageMapper = garbageMapper;
        this.garbageCategoryService = garbageCategoryService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public int addGarbage(Garbage garbage) {
        return garbageMapper.insert(garbage);
    }

    @Override
    public int deleteGarbage(long id) {
        return garbageMapper.deleteById(id);
    }

    @Override
    public int deleteGarbageList(List<Long> ids) {
        return garbageMapper.deleteBatchIds(ids);
    }

    @Override
    public GarbageVO getGarbageById(long id) {
        GarbageVO garbageVO = new GarbageVO();
        //1.先查询redis缓存 如果存在直接返回
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(GARBAGE_LIST_KEY + ":" + id);
        if (ObjectUtil.isNotEmpty(entries)) {
            garbageVO = BeanUtil.fillBeanWithMap(
                    entries,
                    new GarbageVO(),
                    CopyOptions.create().setIgnoreNullValue(true));
        } else {
            //2.如果不存在 查询mysql获取垃圾信息
            Garbage garbage = garbageMapper.getGarbageById(id);
            //3.如果mysql中存在加入缓存并返回,否则缓存空对象
            Map<String, Object> map = BeanUtil.beanToMap(new GarbageVO(), new HashMap<>(), CopyOptions.create());
            if (ObjectUtil.isNotNull(garbage)) {
                String category = garbage.getGarbageCategory().getName();
                BeanUtil.copyProperties(garbage, garbageVO, CopyOptions.create().setIgnoreNullValue(true));
                garbageVO.setCategory(category);

                map = BeanUtil.beanToMap(
                        garbageVO,
                        new HashMap<>(),
                        CopyOptions.create()
                                .setIgnoreNullValue(true)
                                .setFieldValueEditor((name, value) -> {
                                    if (ObjectUtil.isNull(value)) {
                                        return "";
                                    }
                                    return value.toString();
                                }));

            }
            //4.设置缓存
            stringRedisTemplate.opsForHash().putAll(GARBAGE_LIST_KEY + ":" + id, map);
            stringRedisTemplate.expire(GARBAGE_LIST_KEY + ":" + id, GARBAGE_LIST_KEY_TTL_SECOND + RandomUtil.randomLong(1, 30), TimeUnit.SECONDS);
        }
        return garbageVO;
    }

    @Override
    public List<GarbageVO> getAllGarbageByKeyword(String keyword, String isHighlight) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //多条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //对垃圾名称和垃圾分类进行检索
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.matchQuery("category", keyword));

        sourceBuilder.query(boolQueryBuilder);

        if("true".equals(isHighlight)) {
            //关键词高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    //垃圾名称涉及的关键词 红色
                    .field("name")
                    .preTags("<font color='#F56C6C'>")
                    .postTags("</font>");

            sourceBuilder.highlighter(highlightBuilder);
        }

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayList<GarbageVO> garbageVOList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            //原来的数据
            Map<String, Object> source = hit.getSourceAsMap();
            if(name != null)
            {
                StringBuilder stringBuilder = new StringBuilder();
                Text[] fragments = name.fragments();
                for (Text text : fragments) {
                    stringBuilder.append(text);
                }
                //用高亮的name替换原来的name字段
                source.put("name", stringBuilder.toString());
            }
            GarbageVO garbageVO = BeanUtil.fillBeanWithMap(
                    source,
                    new GarbageVO(),
                    CopyOptions.create().setIgnoreNullValue(true));
            garbageVOList.add(garbageVO);
        }
        return garbageVOList;
    }

    @Override
    public List<GarbageVO> getGarbageByKeyword(String keyword, int pageNum, int pageSize) throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //多条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //对垃圾名称和垃圾分类进行检索
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", keyword))
                        .should(QueryBuilders.matchQuery("category", keyword));

        //分页展示
        sourceBuilder.query(boolQueryBuilder)
                .from((pageNum - 1) * pageSize)
                .size(pageSize);

        //关键词高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                //垃圾名称涉及的关键词 红色
                .field("name")
                .preTags("<font color='#F56C6C'>")
                .postTags("</font>");

        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayList<GarbageVO> garbageVOList = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            //原来的数据
            Map<String, Object> source = hit.getSourceAsMap();
            if(name != null)
            {
                StringBuilder stringBuilder = new StringBuilder();
                Text[] fragments = name.fragments();
                for (Text text : fragments) {
                    stringBuilder.append(text);
                }
                //用高亮的name替换原来的name字段
                source.put("name", stringBuilder.toString());
            }
            GarbageVO garbageVO = BeanUtil.fillBeanWithMap(
                    source,
                    new GarbageVO(),
                    CopyOptions.create().setIgnoreNullValue(true));
            garbageVOList.add(garbageVO);
        }
        return garbageVOList;
    }

    @Override
    public long getGarbageCountByKeyword(String keyword) throws IOException {
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //多条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //对垃圾名称和垃圾分类进行检索
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.matchQuery("category", keyword));

        sourceBuilder.query(boolQueryBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //数据总条数
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        return totalHits.value;
    }

    @Override
    public List<GarbageVO> getAllGarbageList() {
        List<Garbage> garbageList = garbageMapper.getGarbageList(null, null, null, null);
        return transfer(garbageList);
    }

    @Override
    public List<GarbageVO> getGarbageList(int pageNum, int pageSize) {
        List<Garbage> list = garbageMapper.getGarbageList(null, null, (pageNum - 1) * pageSize, pageSize);
        return transfer(list);
    }

    @Override
    public List<GarbageVO> geAllGarbageListByName(String garbageName) {
        List<Garbage> garbageList = garbageMapper.getGarbageList(garbageName, null, null, null);
        return transfer(garbageList);
    }

    @Override
    public List<GarbageVO> getGarbageListByNameOrCategory(String garbageName, String categoryName, int pageNum, int pageSize) {
        List<Garbage> garbageList = garbageMapper.getGarbageList(garbageName, categoryName, (pageNum - 1) * pageSize, pageSize);
        return transfer(garbageList);
    }

    @Override
    public long getGarbageCount(String garbageName, String categoryName) {
        List<Garbage> garbageList = garbageMapper.getGarbageList(garbageName, categoryName, null, null);
        return garbageList.size();
    }

    @Override
    public List<Map<String, Object>> getGarbageCountGroupByCategory() {
        List<GarbageCategoryVO> garbageCategoryList = garbageCategoryService.getAllGarbageCategoryList();
        //初始化分分组
        List<Map<String, Object>> result = new ArrayList<>(garbageCategoryList.size());
        for (GarbageCategoryVO g : garbageCategoryList) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", g.getName());
            map.put("value", 0);
            result.add(map);
        }
        List<Map<String, Object>> maps = garbageMapper.getGarbageCountGroupByCategory();
        maps.forEach(m -> {
            result.forEach(r -> {
                if (r.get("name").equals(m.get("name"))) {
                    r.put("value", m.get("value"));
                }
            });
        });
        return result;
    }


    @Override
    public int updateGarbage(Garbage garbage) {
        return garbageMapper.updateById(garbage);
    }

    private List<GarbageVO> transfer(List<Garbage> garbageList){
        ArrayList<GarbageVO> garbageVOList = new ArrayList<>();
        garbageList.forEach(g -> {
            String category = g.getGarbageCategory().getName();
            GarbageVO garbageVO = BeanUtil.copyProperties(g, GarbageVO.class);
            garbageVO.setCategory(category);
            garbageVOList.add(garbageVO);
        });
        return garbageVOList;
    }

}
