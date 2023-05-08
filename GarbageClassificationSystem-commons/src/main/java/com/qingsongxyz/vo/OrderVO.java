package com.qingsongxyz.vo;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.qingsongxyz.pojo.OrderDetails;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO extends Model<OrderVO> {

    private Long id;

    private Long userId;

    private Integer sum;

    private LocalDateTime createTime;

    private List<OrderDetails> orderDetailsList;

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}
