package cn.itcast.stat.service;

import java.util.List;
import java.util.Map;

public interface StatService {
    /**
     * 需求1：根据生产厂家统计货物销售金额
     */
    List<Map<String,Object>> getFactoryData(String companyId);


    /**
     * 需求2：商品销售统计排行
     */
    List<Map<String,Object>> getProductData(String companyId);

    /**
     * 需求3： 系统访问压力图
     */
    List<Map<String,Object>> getOnlineData(String companyId);
}
