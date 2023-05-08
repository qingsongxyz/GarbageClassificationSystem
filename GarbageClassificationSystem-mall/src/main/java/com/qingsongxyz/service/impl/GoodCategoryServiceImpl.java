package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.GoodCategoryMapper;
import com.qingsongxyz.pojo.GoodCategory;
import com.qingsongxyz.service.GoodCategoryService;
import com.qingsongxyz.vo.GoodCategoryVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品种类表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Service
public class GoodCategoryServiceImpl extends ServiceImpl<GoodCategoryMapper, GoodCategory> implements GoodCategoryService {

    private final GoodCategoryMapper goodCategoryMapper;

    public GoodCategoryServiceImpl(GoodCategoryMapper goodCategoryMapper) {
        this.goodCategoryMapper = goodCategoryMapper;
    }

    @Override
    public int addGoodCategory(GoodCategory goodCategory) {
        return goodCategoryMapper.insert(goodCategory);
    }

    @Override
    public int deleteGoodCategory(long id) {
        return goodCategoryMapper.deleteById(id);
    }

    @Override
    public int deleteGoodCategoryList(List<Long> ids) {
        return goodCategoryMapper.deleteBatchIds(ids);
    }

    @Override
    public List<GoodCategoryVO> getGoodCategoryList(int pageNum, int pageSize) {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.getGoodCategoryList(null, (pageNum - 1) * pageSize, pageSize);
        return transfer(goodCategoryList);
    }

    @Override
    public List<GoodCategoryVO> getGoodCategoryListByCategory(String category, int pageNum, int pageSize) {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.getGoodCategoryList(category, (pageNum - 1) * pageSize, pageSize);
        return transfer(goodCategoryList);
    }

    @Override
    public List<GoodCategoryVO> getAllGoodCategoryListByCategory(String category) {
        List<GoodCategory> goodCategoryList = goodCategoryMapper.selectList(new QueryWrapper<GoodCategory>().like("category", category).eq("deleted", 0));
        return transfer(goodCategoryList);
    }

    @Override
    public List<GoodCategoryVO> getAllGoodCategoryList() {
        List<GoodCategory> goodCategoryList = list();
        return transfer(goodCategoryList);
    }

    @Override
    public int getAllGoodCategoryCount() {
        return goodCategoryMapper.getGoodCategoryCount(null);
    }

    @Override
    public int getGoodCategoryCountByCategory(String category) {
        return goodCategoryMapper.getGoodCategoryCount(category);
    }

    @Override
    public int updateGoodCategory(GoodCategory goodCategory) {
        return goodCategoryMapper.updateById(goodCategory);
    }

    /**
     * 将商品种类集合转换为商品种类VO集合
     * @param goodCategoryList 商品种类集合
     * @return 商品种类VO集合
     */
    private List<GoodCategoryVO> transfer(List<GoodCategory> goodCategoryList) {
        List<GoodCategoryVO> goodCategoryVOList = new ArrayList<>();
        goodCategoryList.forEach(c -> {
            GoodCategoryVO goodCategoryVO = BeanUtil.copyProperties(c, GoodCategoryVO.class);
            goodCategoryVOList.add(goodCategoryVO);
        });
        return goodCategoryVOList;
    }
}
