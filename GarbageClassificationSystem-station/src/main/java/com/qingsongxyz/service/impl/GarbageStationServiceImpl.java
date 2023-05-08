package com.qingsongxyz.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.qingsongxyz.pojo.Address;
import com.qingsongxyz.pojo.GarbageStation;
import com.qingsongxyz.service.GarbageStationService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 垃圾回收站 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-2-08
 */
@Slf4j
@Service
public class GarbageStationServiceImpl implements GarbageStationService {

    private static final String COLLECTION_NAME = "garbageStations";

    private final MongoTemplate mongoTemplate;

    public GarbageStationServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public int addGarbageStation(GarbageStation garbageStation) {
        try {
            mongoTemplate.insert(garbageStation, COLLECTION_NAME);
            return 1;
        } catch (Exception e) {
            log.error("GarbageStationServiceImpl addGarbageStation Exception:{}", e.getMessage());
        }
        return 0;
    }

    @Override
    public int deleteGarbageStation(String id) {
        ObjectId objectId = new ObjectId(id);
        DeleteResult result = mongoTemplate.remove(new Query(Criteria.where("id").is(objectId)), GarbageStation.class);
        return result.wasAcknowledged() ? 1 : 0;
    }

    @Override
    public GarbageStation getGarbageStationById(String id) {
        ObjectId objectId = new ObjectId(id);
        return mongoTemplate.findById(objectId, GarbageStation.class);
    }

    @Override
    public List<GarbageStation> getGarbageStationListByAddress(Address address) {
        Criteria criteria = new Criteria();
        GarbageStation garbageStation = new GarbageStation();
        garbageStation.setAddress(address);

        //设置样本 忽略null值,省份和城市需要完全一致,其他的地理信息模糊匹配
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("address.province", m -> m.stringMatcher(ExampleMatcher.StringMatcher.EXACT))
                .withMatcher("address.city", m -> m.stringMatcher(ExampleMatcher.StringMatcher.EXACT))
                .withMatcher("address.district", m -> m.stringMatcher(ExampleMatcher.StringMatcher.CONTAINING))
                .withMatcher("address.street", m -> m.stringMatcher(ExampleMatcher.StringMatcher.CONTAINING))
                .withMatcher("address.streetNumber", m -> m.stringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        Example<GarbageStation> example = Example.of(garbageStation, matcher);
        criteria.alike(example);
        return mongoTemplate.find(new Query(criteria), GarbageStation.class);
    }

    @Override
    public List<GarbageStation> getNearGarbageStationList(double[] coordinates, double distance) {
        Point point = new Point(coordinates[0], coordinates[1]);
        //匹配在圆形范围内的所有垃圾回收站
        Criteria criteria = Criteria.where("coordinates")
                .within(new Circle(point, new Distance(distance, Metrics.KILOMETERS)));
        return mongoTemplate.find(new Query(criteria), GarbageStation.class);
    }

    @Override
    public int updateGarbageStation(GarbageStation garbageStation) {
        Update update = new Update();

        if (ObjectUtil.isNotNull(garbageStation.getAddress())) {
            Address address = garbageStation.getAddress();
            if (StrUtil.isNotBlank(address.getProvince())) {
                update.set("address.province", address.getProvince());
            }
            if (StrUtil.isNotBlank(address.getCity())) {
                update.set("address.city", address.getCity());
            }
            if (StrUtil.isNotBlank(address.getDistrict())) {
                update.set("address.district", address.getDistrict());
            }
            if (StrUtil.isNotBlank(address.getStreet())) {
                update.set("address.street", address.getStreet());
            }
            if (StrUtil.isNotBlank(address.getStreetNumber())) {
                update.set("address.streetNumber", address.getStreetNumber());
            }
        }
        if (ObjectUtil.isNotEmpty(garbageStation.getCoordinates())) {
            update.set("coordinates", garbageStation.getCoordinates());
        }
        if (ObjectUtil.isNotEmpty(garbageStation.getStatus())) {
            update.set("status", garbageStation.getStatus());
        }
        ObjectId objectId = new ObjectId(garbageStation.getId());
        UpdateResult updateResult = mongoTemplate.updateFirst(new Query(Criteria.where("id").is(objectId)),
                update,
                GarbageStation.class);
        return updateResult.wasAcknowledged() ? 1 : 0;
    }
}
