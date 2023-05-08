package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class GarbageDeliveryVO extends Model<GarbageDeliveryVO> {

    private Long id;

    private Long userId;

    private Integer total;

    private String stationAddress;

    private Integer status;

    private String addition;

    private LocalDateTime createTime;

    private UserVO userVO;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
