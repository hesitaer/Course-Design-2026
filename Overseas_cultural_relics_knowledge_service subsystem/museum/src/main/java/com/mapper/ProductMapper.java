package com.mapper;

import com.entity.Product;
import com.entity.ProductQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    
    /**
     * 多条件查询文物列表（不分页）
     * @param queryDTO 查询条件
     * @return 文物列表
     */
    List<Product> findByConditions(@Param("query") ProductQueryDTO queryDTO);
    
    /**
     * 多条件查询文物列表（分页）
     * @param queryDTO 查询条件
     * @param offset 偏移量
     * @param limit 每页大小
     * @return 文物列表
     */
    List<Product> findByConditionsWithPage(@Param("query") ProductQueryDTO queryDTO, @Param("offset") Integer offset, @Param("limit") Integer limit);
    
    /**
     * 统计符合条件的文物数量
     * @param queryDTO 查询条件
     * @return 数量
     */
    Integer countByConditions(@Param("query") ProductQueryDTO queryDTO);
    
    /**
     * 关键词搜索
     * @param keyword 关键词
     * @return 文物列表
     */
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
