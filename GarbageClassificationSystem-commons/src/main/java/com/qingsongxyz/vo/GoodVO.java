package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.pojo.Storage;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class GoodVO extends Model<GoodVO> {

    private Long id;

    private String name;

    private Long categoryId;

    private String image;

    private Integer score;

    private GoodCategoryVO goodCategoryVO;

    private StorageVO storage;

    @Override
    public Serializable pkVal() {
        return super.pkVal();
    }
}
