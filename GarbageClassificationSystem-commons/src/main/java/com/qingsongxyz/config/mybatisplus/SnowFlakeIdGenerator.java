package com.qingsongxyz.config.mybatisplus;

import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import java.net.InetAddress;

/**
 * 自定义ID生成器
 */
@Component
@ConditionalOnClass({MybatisPlusInterceptor.class})
public class SnowFlakeIdGenerator implements IdentifierGenerator {
    private final Sequence sequence;

    public SnowFlakeIdGenerator() {
        this.sequence = new Sequence(null);
    }

    public SnowFlakeIdGenerator(InetAddress inetAddress) {
        this.sequence = new Sequence(inetAddress);
    }

    public SnowFlakeIdGenerator(long workerId, long dataCenterId) {
        this.sequence = new Sequence(workerId, dataCenterId);
    }

    public SnowFlakeIdGenerator(Sequence sequence) {
        this.sequence = sequence;
    }

    @Override
    public Long nextId(Object entity) {
        return sequence.nextId();
    }
}
