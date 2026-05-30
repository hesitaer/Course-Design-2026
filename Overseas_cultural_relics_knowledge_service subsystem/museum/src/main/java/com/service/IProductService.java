package com.service;

import com.entity.Product;
import com.entity.StatisticsDTO;
import com.entity.TimelineDTO;
import java.util.List;

public interface IProductService {
    
    Product findByProductId(Integer museumId, String objectId);
    
    List<Product> findAllProducts();

    StatisticsDTO getStatistics();

    TimelineDTO getTimeline();
}
