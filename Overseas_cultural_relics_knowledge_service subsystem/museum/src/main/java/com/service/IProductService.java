package com.service;

import com.entity.Product;

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
     * 分页查询文物列表（支持筛选和排序）
     * @param params 筛选条件和排序参数
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页数量
     * @return 文物列表
     */
    List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize);
    
    /**
     * 查询文物总数
     * @param params 筛选条件
     * @return 文物总数
     */
    Integer countByParams(Map<String, Object> params);
    
    /**
     * 根据多个ID批量查询文物（用于对比功能）
     * @param ids 文物ID列表（格式：museumId_objectId）
     * @return 文物列表
     */
    List<Product> findByIds(List<String> ids);
    
    /**
     * 获取所有文物类型
     * @return 类型列表
     */
    List<String> getAllTypes();
    
    /**
     * 获取所有朝代
     * @return 朝代列表
     */
    List<String> getAllDynasties();
    
    /**
     * 获取所有博物馆
     * @return 博物馆列表
     */
    List<String> getAllMuseums();
    
    /**
     * 获取所有材质
     * @return 材质列表
     */
    List<String> getAllMaterials();
}
