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
public class GoodCategoryVO extends Model<GoodCategoryVO> {

    private Long id;

    private String category;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
