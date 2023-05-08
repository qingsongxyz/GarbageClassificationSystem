package com.qingsongxyz.listener;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qingsongxyz.excel.GoodTemplate;
import com.qingsongxyz.pojo.*;
import com.qingsongxyz.service.GoodCategoryService;
import com.qingsongxyz.service.GoodService;
import com.qingsongxyz.service.feignService.OSSFeignService;
import com.qingsongxyz.util.ExcelImportDataValidUtil;
import com.qingsongxyz.vo.GoodCategoryVO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class GoodImportListener extends AnalysisEventListener<GoodTemplate> {

    private String endpoint;

    private String bucket;

    private GoodService goodService;

    private GoodCategoryService goodCategoryService;

    private OSSFeignService ossFeignService;

    private final Map<Integer, Good> goodList = new HashMap<>();

    @SneakyThrows
    @Override
    public void invoke(GoodTemplate goodTemplate, AnalysisContext context) {
        //获取当前分析数据所在行(加上表头)
        ReadRowHolder holder = context.readRowHolder();
        int row = holder.getRowIndex() + 1;

        //校验当前分析行数据
        ExcelImportDataValidUtil.valid(goodTemplate, row);

        Good good = new Good();
        good.setName(goodTemplate.getName());
        good.setScore(goodTemplate.getScore());
        good.setImage(goodTemplate.getImage());
        good.setStorage(new Storage().setStorage(goodTemplate.getStorage()));

        String url = goodTemplate.getImage();
        if (!url.startsWith("https://garbage-bucket.oss-cn-shanghai.aliyuncs.com/gcs")) {
            String host = "https://" + bucket + "." + endpoint;
            String realPath = host + "/gcs/good/" + good.getName() + ".png";
            good.setImage(realPath);

            if (ossFeignService != null) {
                //导入垃圾图片到OSS
                CommonResult result = ossFeignService.downloadUrlImage(good.getName(), url, "good");
                if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                    throw new DegradeException(result.getMessage());
                }
            }
        }

        if (goodCategoryService != null) {
            List<GoodCategoryVO> goodCategoryVOList = goodCategoryService.getAllGoodCategoryListByCategory(goodTemplate.getCategory());
            if (!ObjectUtils.isEmpty(goodCategoryVOList)) {
                good.setCategoryId(goodCategoryVOList.get(0).getId());
            }
        }
        goodList.put(row, good);
    }

    @SneakyThrows
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("完成Excel读取, 开始存入数据库...");
        //保存商品名称重复的行
        List<Integer> repeatRowList = new ArrayList<>();
        //保存插入商品数据失败的行
        List<Integer> errorRowList = new ArrayList<>();

        if (goodService != null) {
            for (Map.Entry<Integer, Good> entry : goodList.entrySet()) {
                Integer row = entry.getKey();
                Good good = entry.getValue();

                //查询商品名称是否存在
                long count = goodService.count(new QueryWrapper<Good>().eq("name", good.getName()));
                if (count > 0) {
                    repeatRowList.add(row);
                    continue;
                }
                int result = goodService.addGood(good);
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
            throw new Exception("第" + builder + "行商品名称已存在, excel导入失败!!!");
        }

        if (errorRowList.size() != 0) {
            StringBuilder builder = new StringBuilder();
            errorRowList.forEach(r -> {
                builder.append(r).append("、");
            });
            builder.deleteCharAt(builder.length() - 1);
            throw new Exception("第" + builder + "行商品信息插入数据库错误, excel导入失败!!!");
        }

        //清空用户数据
        goodList.clear();
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
