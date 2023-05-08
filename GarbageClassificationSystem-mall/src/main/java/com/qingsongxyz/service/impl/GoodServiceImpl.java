package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.excel.GoodTemplate;
import com.qingsongxyz.mapper.GoodMapper;
import com.qingsongxyz.pojo.Good;
import com.qingsongxyz.pojo.Storage;
import com.qingsongxyz.service.GoodService;
import com.qingsongxyz.service.StorageService;
import com.qingsongxyz.vo.GoodVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-11
 */
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {

    private final GoodMapper goodMapper;

    private final StorageService storageService;

    private final CanalServiceImpl canalService;

    public GoodServiceImpl(GoodMapper goodMapper, StorageService storageService, CanalServiceImpl canalService) {
        this.goodMapper = goodMapper;
        this.storageService = storageService;
        this.canalService = canalService;
    }

    @PostConstruct
    void listen() {
        try {
            canalService.run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addGood(Good good) {
        int result = goodMapper.insert(good);
        if (result == 1) {
            Storage storage = good.getStorage();
            storage.setGoodId(good.getId());
            return storageService.addStorage(storage);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteGood(long id) {
        int result = goodMapper.deleteById(id);
        if (result == 1) {
            return storageService.deleteStorage(id);
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int deleteGoodList(List<Long> ids) {
        boolean flag = true;
        for (Long id : ids) {
            int res = deleteGood(id);
            if (res == 0) {
                flag = false;
            }
        }
        return flag ? 1 : 0;
    }

    @Override
    public List<GoodVO> getGoodList(int pageNum, int pageSize) {
        List<Good> goodList = goodMapper.getGoodList(null, null, (pageNum - 1) * pageSize, pageSize);
        return transfer(goodList);
    }

    @Override
    public List<GoodVO> getGoodListByNameOrCategory(String name, String category, int pageNum, int pageSize) {
        List<Good> goodList = goodMapper.getGoodList(name, category, (pageNum - 1) * pageSize, pageSize);
        return transfer(goodList);
    }

    @Override
    public List<GoodVO> getAllGoodListByName(String name) {
        List<Good> goodList = goodMapper.selectList(new QueryWrapper<Good>().like("name", name).eq("deleted", 0));
        return transfer(goodList);
    }

    @Override
    public List<GoodTemplate> getAllGoodList() {
        List<Good> goodList = goodMapper.getGoodList(null, null, null, null);
        ArrayList<GoodTemplate> list = new ArrayList<>();
        goodList.forEach(g -> {
            GoodTemplate goodTemplate = new GoodTemplate();
            goodTemplate.setId(g.getId());
            goodTemplate.setName(g.getName());
            goodTemplate.setCategory(g.getGoodCategoryVO().getCategory());
            goodTemplate.setImage(g.getImage());
            goodTemplate.setScore(g.getScore());
            goodTemplate.setStorage(g.getStorage().getStorage());
            list.add(goodTemplate);
        });
        return list;
    }

    @Override
    public int getAllGoodCount() {
        return goodMapper.getGoodCount(null, null);
    }

    @Override
    public int getGoodByNameOrCategory(String name, String category) {
        return goodMapper.getGoodCount(name, category);
    }

    @Override
    public int updateGood(Good good) {
        int result = goodMapper.updateById(good);
        Storage storage = good.getStorage();
        if (ObjectUtil.isNotNull(storage)) {
            storageService.update(new UpdateWrapper<Storage>().eq("good_id", good.getId()).set("storage", storage.getStorage()));
        }
        return result;
    }

    /**
     * 将商品集合转换商品VO集合
     *
     * @param goodList 商品集合
     * @return 商品VO集合
     */
    private List<GoodVO> transfer(List<Good> goodList) {
        List<GoodVO> goodVOList = new ArrayList<>(goodList.size());
        goodList.forEach(g -> {
            GoodVO goodVO = BeanUtil.copyProperties(g, GoodVO.class);
            goodVOList.add(goodVO);
        });

        return goodVOList;
    }
}
