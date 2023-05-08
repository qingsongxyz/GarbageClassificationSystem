package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qingsongxyz.pojo.GarbageCategory;
import com.qingsongxyz.mapper.GarbageCategoryMapper;
import com.qingsongxyz.service.GarbageCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.vo.GarbageCategoryVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import static com.qingsongxyz.constant.RedisConstant.*;

/**
 * <p>
 * 垃圾分类表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-06
 */
@Service
public class GarbageCategoryServiceImpl extends ServiceImpl<GarbageCategoryMapper, GarbageCategory> implements GarbageCategoryService {

    private final StringRedisTemplate stringRedisTemplate;

    private final GarbageCategoryMapper garbageCategoryMapper;

    public GarbageCategoryServiceImpl(StringRedisTemplate stringRedisTemplate, GarbageCategoryMapper garbageCategoryMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.garbageCategoryMapper = garbageCategoryMapper;
    }

    @Override
    public int addGarbageCategory(GarbageCategory garbageCategory) {
        return garbageCategoryMapper.insert(garbageCategory);
    }

    @Override
    public int deleteGarbageCategory(long id) {
        return garbageCategoryMapper.deleteById(id);
    }

    @Override
    public int deleteGarbageCategoryList(List<Long> ids) {
        return garbageCategoryMapper.deleteBatchIds(ids);
    }

    @Override
    public GarbageCategoryVO getGarbageCategoryById(long id) {
        GarbageCategory garbageCategory = garbageCategoryMapper.selectById(id);
        GarbageCategoryVO garbageCategoryVO = BeanUtil.copyProperties(garbageCategory, GarbageCategoryVO.class);
        return garbageCategoryVO;
    }

    @Override
    public List<GarbageCategoryVO> getAllGarbageCategoryList() {
        List<GarbageCategory> garbageCategoryList;
        Set<String> categoryList = stringRedisTemplate.opsForSet().members(GARBAGE_CATEGORY_LIST_KEY);
        if(ObjectUtil.isNotEmpty(categoryList)) {
            garbageCategoryList = categoryList.stream().map(c -> JSON.parseObject(c, GarbageCategory.class)).collect(Collectors.toList());
        }else {
            garbageCategoryList = this.list();
            String[] values = garbageCategoryList.stream().map(JSON::toJSONString).toArray(String[]::new);
            stringRedisTemplate.opsForSet().add(GARBAGE_CATEGORY_LIST_KEY, values);
            stringRedisTemplate.expire(GARBAGE_CATEGORY_LIST_KEY, GARBAGE_CATEGORY_LIST_KEY_TTL_SECOND, TimeUnit.SECONDS);
        }
        return transfer(garbageCategoryList);
    }

    @Override
    public List<GarbageCategoryVO> getGarbageCategoryList(int pageNum, int pageSize) {
        //分页
        Page<GarbageCategory> page = Page.of(pageNum, pageSize);
        Page<GarbageCategory> garbageCategoryPage = this.page(page);
        List<GarbageCategory> garbageCategoryList = garbageCategoryPage.getRecords();
        return transfer(garbageCategoryList);
    }

    @Override
    public List<GarbageCategoryVO> getAllGarbageCategoryListByName(String name) {
        //分类名称模糊查询
        QueryWrapper<GarbageCategory> wrapper = new QueryWrapper<>();
        wrapper.like("name", name);
        List<GarbageCategory> garbageCategoryList = this.list(wrapper);
        return transfer(garbageCategoryList);
    }

    @Override
    public List<GarbageCategoryVO> getGarbageCategoryListByName(String name, int pageNum, int pageSize) {
        //分页
        Page<GarbageCategory> page = Page.of(pageNum, pageSize);
        //分类名称模糊查询
        QueryWrapper<GarbageCategory> wrapper = new QueryWrapper<>();
        wrapper.like("name", name);
        Page<GarbageCategory> garbageCategoryPage = this.page(page, wrapper);
        List<GarbageCategory> garbageCategoryList = garbageCategoryPage.getRecords();
        return transfer(garbageCategoryList);
    }

    @Override
    public long getGarbageCategoryCount(String name) {
        if (StrUtil.isEmpty(name)) {
            return this.count();
        } else {
            //分类名称模糊查询
            QueryWrapper<GarbageCategory> wrapper = new QueryWrapper<>();
            wrapper.like("name", name);
            return this.count(wrapper);
        }
    }

    @Override
    public int updateGarbageCategory(GarbageCategory garbageCategory) {
        return garbageCategoryMapper.updateById(garbageCategory);
    }

    private List<GarbageCategoryVO> transfer(List<GarbageCategory> garbageCategoryList) {
        ArrayList<GarbageCategoryVO> garbageCategoryVOList = new ArrayList<>();
        garbageCategoryList.forEach(c -> {
            GarbageCategoryVO garbageCategoryVO = BeanUtil.copyProperties(c, GarbageCategoryVO.class);
            garbageCategoryVOList.add(garbageCategoryVO);
        });
        return garbageCategoryVOList;
    }

}
