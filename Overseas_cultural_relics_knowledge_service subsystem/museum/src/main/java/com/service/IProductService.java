package com.service;

import com.entity.Product;

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
}
