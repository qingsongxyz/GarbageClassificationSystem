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
public class MarketDetailsVO extends Model<MarketDetailsVO> {

    private Long id;

    private Long marketId;

    private Long goodId;

    private String goodName;

    private String goodImage;

    private Integer goodScore;

    private Integer count;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
