package com.mapper;

import com.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 文物数据访问层
 * 对应表：artifact
 */
@Repository
@Mapper
public interface ProductMapper {
    
    /**
     * 通过复合主键查询文物详情
     * @param museumId 博物馆ID (1=史密森尼, 2=哈佛, 3=波士顿MFA)
     * @param objectId 文物唯一编号（馆方/EDAN编号）
     * @return Product 文物对象
     */
    Product findByProductId(@Param("museumId") Integer museumId, @Param("objectId") String objectId);
}
