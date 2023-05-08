package com.qingsongxyz.pojo;

import com.qingsongxyz.constraints.Coordinate;
import com.qingsongxyz.validation.CreateGroup;
import com.qingsongxyz.validation.UpdateGroup;
import io.swagger.annotations.ApiModel;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>
 * 垃圾回收站表
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-7
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
@ApiModel(value = "GarbageStation对象", description = "垃圾回收站表")
@Document(value = "garbageStations") //文档名称为garbageStations
public class GarbageStation {

    @Id //唯一id
    @NotBlank(message = "垃圾回收站id不能为空", groups = {UpdateGroup.class})
    private String id;

    //坐标(经纬度)
    @Field
    @NotNull(message = "垃圾回收站坐标不能为空", groups = {CreateGroup.class})
    @Size(message = "垃圾回收站坐标必须为经纬度", min = 2, max = 2, groups = {CreateGroup.class, UpdateGroup.class})
    @Coordinate
    private double[] coordinates;

    @Field //地址
    @Valid
    @NotNull(message = "垃圾回收站地址不能为空", groups = {CreateGroup.class})
    private Address address;

    @Field //垃圾回收站状态(可投递、不可投递[已满])
    @NotNull(message = "垃圾回收站状态不能为空", groups = {CreateGroup.class})
    private Boolean status;

    @Version //乐观锁
    private Long version;
}
