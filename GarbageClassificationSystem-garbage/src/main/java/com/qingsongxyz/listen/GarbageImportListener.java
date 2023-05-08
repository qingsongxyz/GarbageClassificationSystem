package com.qingsongxyz.listen;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Garbage;
import com.qingsongxyz.service.GarbageCategoryService;
import com.qingsongxyz.service.GarbageService;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.util.ExcelImportDataValidUtil;
import com.qingsongxyz.vo.GarbageCategoryVO;
import com.qingsongxyz.vo.GarbageVO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import java.util.*;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class GarbageImportListener extends AnalysisEventListener<GarbageVO> {

    private String endpoint;

    private String bucket;

    private GarbageService garbageService;

    private GarbageCategoryService garbageCategoryService;

    private OSSFeignService ossFeignService;

    private final Map<Integer, Garbage> garbageList = new HashMap<>();

    @SneakyThrows
    @Override
    public void invoke(GarbageVO garbageVO, AnalysisContext context) {
        //获取当前分析数据所在行(加上表头)
        ReadRowHolder holder = context.readRowHolder();
        int row = holder.getRowIndex() + 1;

        //校验当前分析行数据
        ExcelImportDataValidUtil.valid(garbageVO, row);

        Garbage garbage = new Garbage();
        garbage.setName(garbageVO.getName());
        garbage.setUnit(garbageVO.getUnit());
        garbage.setScore(garbageVO.getScore());
        garbage.setImage(garbageVO.getImage());
        String url = garbageVO.getImage();

        if(!url.startsWith("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs")) {
            String host = "https://" + bucket + "." + endpoint;
            String realPath = host + "/gcs/garbage/" + garbageVO.getName() + ".png";
            garbage.setImage(realPath);

            //导入垃圾图片到OSS
            if (ossFeignService != null) {
                CommonResult result = ossFeignService.downloadUrlImage(garbageVO.getName(), garbageVO.getImage(), "garbage");
                if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                    throw new DegradeException(result.getMessage());
                }
            }
        }

        if (garbageCategoryService != null) {
            List<GarbageCategoryVO> garbageCategoryVOList = garbageCategoryService.getAllGarbageCategoryListByName(garbageVO.getCategory());
            if (!ObjectUtils.isEmpty(garbageCategoryVOList)) {
                garbage.setCategoryId(garbageCategoryVOList.get(0).getId());
            }
        }
        garbageList.put(row, garbage);
    }

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("完成Excel读取...");
        //保存垃圾名称重复的行
        List<Integer> repeatRowList = new ArrayList<>();
        //保存插入垃圾数据失败的行
        List<Integer> errorRowList = new ArrayList<>();
        if (garbageService != null) {
            for (Map.Entry<Integer, Garbage> entry : garbageList.entrySet()) {
                Integer row = entry.getKey();
                Garbage garbage = entry.getValue();
                long count = garbageService.count(new QueryWrapper<Garbage>().eq("name", garbage.getName()));
                if (count > 0) {
                    repeatRowList.add(row);
                    continue;
                }
                int result = garbageService.addGarbage(garbage);
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
            throw new Exception("第" + builder + "行垃圾名称已存在, excel导入失败!!!");
        }

        if (errorRowList.size() != 0) {
            StringBuilder builder = new StringBuilder();
            errorRowList.forEach(r -> {
                builder.append(r).append("、");
            });
            builder.deleteCharAt(builder.length() - 1);
            throw new Exception("第" + builder + "行垃圾信息插入数据库错误, excel导入失败!!!");
        }

        //清空垃圾数据
        garbageList.clear();
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
