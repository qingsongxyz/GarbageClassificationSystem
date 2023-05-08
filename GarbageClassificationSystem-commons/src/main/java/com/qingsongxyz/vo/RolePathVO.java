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
public class RolePathVO extends Model<RolePathVO> {

    private Long id;

    private Long roleId;

    private Long pathId;

    private RoleVO roleVO;

    private PathVO pathVO;

    @Override
    public Serializable pkVal() {
        return super.pkVal();
    }
}
