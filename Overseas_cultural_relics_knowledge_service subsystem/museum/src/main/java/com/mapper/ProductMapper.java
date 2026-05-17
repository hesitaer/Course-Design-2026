package com.mapper;

import com.entity.Product;
import com.entity.ProductQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@Mapper
public interface ProductMapper {
    Product findByProduct_id(BigInteger id);
    
    List<Product> findByConditions(ProductQueryDTO queryDTO);
    
    List<Product> findByConditionsWithPage(@Param("queryDTO") ProductQueryDTO queryDTO, 
                                          @Param("offset") Integer offset, 
                                          @Param("pageSize") Integer pageSize);
    
    Integer countByConditions(ProductQueryDTO queryDTO);
    
    List<Product> searchByKeyword(@Param("keyword") String keyword);
}
