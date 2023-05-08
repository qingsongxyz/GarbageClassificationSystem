package com.qingsongxyz.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.ObjectListing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RefreshScope
@Slf4j
@Service
public class CleanOSSImageService {

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKey;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucket;

    @Scheduled(cron = "0 0 0 * * ?")
    public void clean() {
        log.info("定时任务开始,删除OSS过期文件...");
        OSS ossClient = null;

        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

            String prefix = "gcs/classify/";

            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucket, prefix, null, null, 1000);

            //查询oss指定文件夹下的文件
            ObjectListing objects = ossClient.listObjects(listObjectsRequest);

            //存储过期图片key
            List<String> keys = new ArrayList<>();
            objects.getObjectSummaries().forEach(o -> {
                //获取文件最后修改时间
                Date lastModified = o.getLastModified();
                Instant instant = lastModified.toInstant();
                LocalDateTime lastModifiedTime = LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));

                //获取当前时间
                LocalDateTime now = LocalDateTime.now();

                //如果文件过期进行删除操作
                if (lastModifiedTime.isBefore(now)) {
                    keys.add(o.getKey());
                }
            });

            if (!keys.isEmpty()) {
                DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket).withKeys(keys);
                DeleteObjectsResult result = ossClient.deleteObjects(deleteObjectsRequest);
                List<String> deletedObjects = result.getDeletedObjects();
                log.info("删除oss文件是否成功:{}", deletedObjects.size() == keys.size());
            }
        } finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }
    }
}
