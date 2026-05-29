package com.service.Impl;

import com.entity.Product;
import com.mapper.FindImageMapper;
import com.mapper.ProductMapper;
import com.service.IProductService;
import com.service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private FindImageMapper findImageMapper;

    @Override
    public Product findByProductId(Integer museumId, String objectId){
        Product product = productMapper.findByProductId(museumId, objectId);
        if(product == null) {
            throw new ProductNotFoundException("该文物不存在");
        }
        if(product.getImageUrl() != null) {
            String[] address = product.getImageUrl().split(",");
            product.setImageUrl(address[0]);
        }
        return product;
    }

    @Override
    public List<Product> findByPage(Map<String, Object> params, Integer pageNum, Integer pageSize) {
        Integer offset = (pageNum - 1) * pageSize;
        List<Product> products = productMapper.findByPage(params, offset, pageSize);
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
            }
        }
        return products;
    }

    @Override
    public Integer countByParams(Map<String, Object> params) {
        return productMapper.countByParams(params);
    }

    @Override
    public List<Product> findByIds(List<String> ids) {
        List<Product> products = productMapper.findByIds(ids);
        for (Product product : products) {
            if (product.getImageUrl() != null) {
                String[] address = product.getImageUrl().split(",");
                product.setImageUrl(address[0]);
            }
        }
        return products;
    }

    @Override
    public List<String> getAllTypes() {
        return productMapper.getAllTypes();
    }

    @Override
    public List<String> getAllDynasties() {
        return productMapper.getAllDynasties();
    }

    @Override
    public List<String> getAllMuseums() {
        return productMapper.getAllMuseums();
    }

    @Override
    public List<String> getAllMaterials() {
        return productMapper.getAllMaterials();
    }
}
