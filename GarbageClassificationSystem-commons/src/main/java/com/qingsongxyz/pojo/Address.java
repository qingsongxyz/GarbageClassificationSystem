package com.qingsongxyz.pojo;

import com.qingsongxyz.constraints.City;
import com.qingsongxyz.constraints.Province;
import com.qingsongxyz.validation.CreateGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Address {

    //省份
    @Province
    @NotBlank(message = "垃圾回收站所属省份不能为空", groups = {CreateGroup.class})
    private String province;

    //城市
    @City
    @NotBlank(message = "垃圾回收站所属城市不能为空", groups = {CreateGroup.class})
    private String city;

    //区
    @NotBlank(message = "垃圾回收站所属区不能为空", groups = {CreateGroup.class})
    private String district;

    //街道
    @NotBlank(message = "垃圾回收站所属街道不能为空", groups = {CreateGroup.class})
    private String street;

    //号
    @NotBlank(message = "垃圾回收站所属街道号不能为空", groups = {CreateGroup.class})
    private String streetNumber;

    @Override
    public String toString() {
        return this.province + this.city + this.district + this.street + this.streetNumber;
    }
}
