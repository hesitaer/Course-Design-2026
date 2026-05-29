package com.mapper;

import com.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
     * 分页查询文物列表（支持排序）
     * @param params 包含筛选条件和排序参数
     * @param offset 偏移量
     * @param limit 每页数量
     * @return 文物列表
     */
    List<Product> findByPage(@Param("params") Map<String, Object> params, 
                              @Param("offset") Integer offset, 
                              @Param("limit") Integer limit);
    
    /**
     * 查询文物总数
     * @param params 筛选条件
     * @return 文物总数
     */
    Integer countByParams(@Param("params") Map<String, Object> params);
    
    /**
     * 根据多个ID批量查询文物
     * @param ids 文物ID列表（格式：museumId_objectId）
     * @return 文物列表
     */
    List<Product> findByIds(@Param("ids") List<String> ids);
    
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
