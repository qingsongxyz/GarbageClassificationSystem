package com.qingsongxyz.listen;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.constant.SecurityConstant;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.UserTemplate;
import com.qingsongxyz.exception.UsernameExistException;
import com.qingsongxyz.pojo.Role;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.service.RoleService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.util.ExcelImportDataValidUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class UserImportListener extends AnalysisEventListener<UserTemplate> {

    private String endpoint;

    private String bucket;

    private UserService userService;

    private RoleService roleService;

    private OSSFeignService ossFeignService;

    private final Map<Integer, User> userList = new HashMap<>();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @SneakyThrows
    @Override
    public void invoke(UserTemplate userTemplate, AnalysisContext context) {
        //获取当前分析数据所在行(加上表头)
        ReadRowHolder holder = context.readRowHolder();
        int row = holder.getRowIndex() + 1;

        //校验当前分析行数据
        ExcelImportDataValidUtil.valid(userTemplate, row);

        //封装成User对象
        User user = new User();
        user.setUsername(userTemplate.getUsername());
        user.setPassword(SecurityConstant.PASSWORD_BCRYPT_PREFIX + passwordEncoder.encode("123456"));
        if (StrUtil.isNotBlank(userTemplate.getGender())) {
            user.setGender(userTemplate.getGender());
        }

        if (ObjectUtil.isNotNull(userTemplate.getAge())) {
            user.setAge(userTemplate.getAge());
        }
        if (ObjectUtil.isNotNull(userTemplate.getImage())) {
            user.setImage(userTemplate.getImage());
        }

        String url = userTemplate.getImage();
        if (!url.startsWith("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs")) {
            String host = "https://" + bucket + "." + endpoint;
            String realPath = host + "/gcs/user/" + user.getUsername() + ".png";
            user.setImage(realPath);

            //导入用户图片到OSS
            if (ossFeignService != null) {
                CommonResult result = ossFeignService.downloadUrlImage(user.getUsername(), url, "user");
                if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                    throw new DegradeException(result.getMessage());
                }
            }
        }
        userList.put(row, user);
    }

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("完成Excel读取, 开始存入数据库...");
        //保存用户名重复的行
        List<Integer> repeatRowList = new ArrayList<>();
        //保存插入用户数据失败的行
        List<Integer> errorRowList = new ArrayList<>();
        if (userService != null && roleService != null) {
            //查询guest角色对应的id
            Role guest = roleService.getOne(new QueryWrapper<Role>().eq("role", "guest"));
            for (Map.Entry<Integer, User> entry : userList.entrySet()) {
                Integer row = entry.getKey();
                User user = entry.getValue();
                //查询用户名是否存在
                long count = userService.count(new QueryWrapper<User>().eq("username", user.getUsername()));
                if (count > 0) {
                    repeatRowList.add(row);
                    continue;
                }
                int result = userService.addUserAndRole(user, guest.getId());
                if (result == 0) {
                    errorRowList.add(row);
                }
            }
        }

        if (repeatRowList.size() != 0) {
            StringBuilder builder = new StringBuilder();
            repeatRowList.forEach(r -> {
                builder.append(r).append("、");
            });
            builder.deleteCharAt(builder.length() - 1);
            throw new UsernameExistException("第" + builder + "行用户名已存在, excel导入失败!!!");
        }

        if (errorRowList.size() != 0) {
            StringBuilder builder = new StringBuilder();
            errorRowList.forEach(r -> {
                builder.append(r).append("、");
            });
            builder.deleteCharAt(builder.length() - 1);
            throw new Exception("第" + builder + "行用户信息插入数据库错误, excel导入失败!!!");
        }

        //清空用户数据
        userList.clear();
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        //处理数据转化异常
        if (exception instanceof ExcelDataConvertException) {
            //获取出错的单元格行列数
            Integer columnIndex = ((ExcelDataConvertException) exception).getColumnIndex() + 1;
            Integer rowIndex = ((ExcelDataConvertException) exception).getRowIndex() + 1;
            String message = String.format("第%s行, 第%s列数据格式有误, excel导入失败!!!", rowIndex, columnIndex);
            throw new Exception(message);
        } else {
            throw exception;
        }
    }
}
