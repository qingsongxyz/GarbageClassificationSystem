package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class GarbageDeliveryDetailsVO extends Model<GarbageDeliveryDetailsVO> {

    private Long id;

    private Long deliveryId;

    private String garbageName;

    private String garbageImage;

    private String garbageCategory;

    private String garbageUnit;

    private Integer garbageScore;

    private Integer count;

    private Integer sum;

    private Integer flag;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
