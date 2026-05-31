package com.service;

import com.entity.Product;
import com.entity.ProductQueryDTO;

import java.util.List;
import java.util.Map;

/**
 * 文物服务接口
 */
public interface IProductService {

    Product findByProductId(Integer museumId, String objectId);

    List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize);

    Integer countByParams(Map<String, Object> params);

    List<Product> findByIds(List<String> ids);

    List<String> getAllTypes();

    List<String> getAllDynasties();

    List<String> getAllMuseums();

    List<String> getAllMaterials();

    List<Product> findByConditions(ProductQueryDTO queryDTO);

    Map<String, Object> findByConditionsWithPage(ProductQueryDTO queryDTO);

    List<Product> searchByKeyword(String keyword);

    String exportToCSV(ProductQueryDTO queryDTO);

    String exportToJSON(ProductQueryDTO queryDTO);
}
