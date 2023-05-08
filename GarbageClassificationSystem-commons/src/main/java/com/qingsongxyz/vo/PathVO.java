package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.pojo.RolePath;
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
public class PathVO extends Model<PathVO>  {

    private Long id;

    private String path;

    private String description;

    private List<RolePathVO> rolePathVOList;

    @Override
    public Serializable pkVal() {
        return id;
    }
}
