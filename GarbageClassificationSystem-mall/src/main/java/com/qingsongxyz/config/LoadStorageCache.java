package com.qingsongxyz.config;

import com.qingsongxyz.pojo.Storage;
import com.qingsongxyz.service.StorageService;
import com.qingsongxyz.vo.StorageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import static com.qingsongxyz.constant.RedisConstant.STORAGE_LIST_KEY;

@Component
@Slf4j
public class LoadStorageCache implements CommandLineRunner {

    private final StringRedisTemplate stringRedisTemplate;

    private final StorageService storageService;

    public LoadStorageCache(StringRedisTemplate stringRedisTemplate, StorageService storageService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.storageService = storageService;
    }

    @Override
    public void run(String... args) {
        List<Storage> storageList = storageService.getStorageList();
        HashMap<String, String> map = new HashMap<>();

        storageList.forEach(s -> {
            map.put(STORAGE_LIST_KEY + ":" + s.getGoodId().toString(), s.getStorage().toString());
        });

        log.info("map:{}", map);
        //存入redis
        stringRedisTemplate.opsForValue().multiSet(map);
    }
}
