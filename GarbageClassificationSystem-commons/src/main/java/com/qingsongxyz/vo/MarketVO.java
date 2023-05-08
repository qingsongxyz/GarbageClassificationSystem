package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MarketVO extends Model<MarketVO> {

    private Long id;

    private Long userId;

    private List<MarketDetailsVO> marketDetailsVOList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
