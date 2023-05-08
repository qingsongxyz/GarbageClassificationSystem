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
public class GarbageCategoryVO extends Model<GarbageCategoryVO> {

    private Long id;

    private String name;

    private String description;

    private String image;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
