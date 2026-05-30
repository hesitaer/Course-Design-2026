package com.mapper;

import com.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface ProductMapper {
    
    Product findByProductId(@Param("museumId") Integer museumId, @Param("objectId") String objectId);
    
    List<Product> findAllProducts();

    Integer countTotal();

    List<Map<String, Object>> countByDynasty();

    List<Map<String, Object>> countByType();

    List<Map<String, Object>> countByMuseum();

    List<Map<String, Object>> countByMaterial();

    List<Map<String, Object>> countByLocation();

    Integer countDistinctDynasty();

    Integer countDistinctMuseum();

    Integer countDistinctType();

    List<Product> findByDynasty(@Param("dynasty") String dynasty);

    List<Map<String, Object>> countByYear();

    List<Map<String, Object>> countDynastyMuseum();

    List<Map<String, Object>> countDynastyMaterial();

    List<Map<String, Object>> countDynastyType();

    List<Map<String, Object>> countMuseumType();

    List<Map<String, Object>> countMuseumMaterial();

    List<Map<String, Object>> countTypeMaterial();

    List<Product> searchProducts(@Param("keyword") String keyword);

    Integer countNoDynasty();

    List<Product> findNoDynasty();
}
