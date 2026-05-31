package com.service;

import com.entity.Product;
import com.entity.StatisticsDTO;
import com.entity.TimelineDTO;
import java.util.List;
import java.util.Map;

public interface IProductService {
    
    Product findByProductId(Integer museumId, String objectId);
    
    List<Product> findAllProducts();

    StatisticsDTO getStatistics();

    TimelineDTO getTimeline();

    List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize);
    
    Integer countByParams(Map<String, Object> params);
    
    List<Product> findByIds(List<String> ids);
    
    List<String> getAllTypes();
    
    List<String> getAllDynasties();
    
    List<String> getAllMuseums();
    
    List<String> getAllMaterials();
}
