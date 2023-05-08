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
public class RoleVO extends Model<RoleVO> {

    private Long id;

    private String role;

    private String name;

    private List<RolePathVO> rolePathVOList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
