package com.service;

import com.entity.Product;
import com.entity.ProductQueryDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface IProductService {
    Product findByProduct_id(BigInteger id);
    
    List<Product> findByConditions(ProductQueryDTO queryDTO);
    
    Map<String, Object> findByConditionsWithPage(ProductQueryDTO queryDTO);
    
    List<Product> searchByKeyword(String keyword);
    
    String exportToCSV(ProductQueryDTO queryDTO);
    
    String exportToJSON(ProductQueryDTO queryDTO);
}
