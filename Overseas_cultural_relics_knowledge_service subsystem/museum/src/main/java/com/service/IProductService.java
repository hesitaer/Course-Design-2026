package com.service;

import com.entity.Product;
import com.entity.ProductQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 文物服务接口
 */
public interface IProductService {
    
    /**
     * 通过复合主键查询文物详情
     * @param museumId 博物馆ID (1=史密森尼, 2=哈佛, 3=波士顿MFA)
     * @param objectId 文物唯一编号
     * @return Product 文物对象
     */
    Product findByProductId(Integer museumId, String objectId);
    
    /**
     * 多条件查询文物列表（不分页）
     * @param queryDTO 查询条件
     * @return 文物列表
     */
    List<Product> findByConditions(ProductQueryDTO queryDTO);
    
    /**
     * 多条件查询文物列表（分页）
     * @param queryDTO 查询条件
     * @return 包含分页信息的Map
     */
    Map<String, Object> findByConditionsWithPage(ProductQueryDTO queryDTO);
    
    /**
     * 关键词搜索
     * @param keyword 关键词
     * @return 文物列表
     */
    List<Product> searchByKeyword(String keyword);
    
    /**
     * 导出为CSV格式
     * @param queryDTO 查询条件
     * @return CSV字符串
     */
    String exportToCSV(ProductQueryDTO queryDTO);
    
    /**
     * 导出为JSON格式
     * @param queryDTO 查询条件
     * @return JSON字符串
     */
    String exportToJSON(ProductQueryDTO queryDTO);
}
