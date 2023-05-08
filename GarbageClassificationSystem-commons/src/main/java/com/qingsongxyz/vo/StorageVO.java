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
public class StorageVO extends Model<StorageVO> {

    private Long id;

    private Long goodId;

    private Integer storage;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
