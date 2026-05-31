package com.mapper;

import com.entity.Product;
import com.entity.ProductQueryDTO;
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

    Product findByProductId(@Param("museumId") Integer museumId, @Param("objectId") String objectId);

    List<Product> findByPage(@Param("params") Map<String, Object> params,
                              @Param("offset") Integer offset,
                              @Param("limit") Integer limit);

    Integer countByParams(@Param("params") Map<String, Object> params);

    List<Product> findByIds(@Param("ids") List<String> ids);

    List<String> getAllTypes();

    List<String> getAllDynasties();

    List<String> getAllMuseums();

    List<String> getAllMaterials();

    List<Product> findByConditions(@Param("query") ProductQueryDTO queryDTO);

    List<Product> findByConditionsWithPage(@Param("query") ProductQueryDTO queryDTO, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Integer countByConditions(@Param("query") ProductQueryDTO queryDTO);

    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
